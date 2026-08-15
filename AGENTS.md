# AGENTS.md

Convenciones obligatorias de este repositorio. Leer antes de crear o mover cualquier archivo.

## Stack

- Quarkus 3.38.2, Java 21, Maven wrapper (`./mvnw.cmd` en Windows).
- Hibernate ORM con Panache, PostgreSQL, Liquibase (`quarkus.liquibase.migrate-at-start=true`).
- Build/verificación rápida: `./mvnw.cmd -q clean compile`.

## Arquitectura hexagonal — encarpetado canónico

Un paquete por bounded context bajo `org.hexarch.<contexto>` (ej. `user`, `auth`, `level`).
**Esta estructura no se negocia ni se simplifica**, aunque una carpeta quede vacía:

```
org.hexarch.<contexto>
├── domain
│   ├── model          # Agregados, entidades y value objects de dominio (records)
│   ├── enums          # Enums de dominio
│   └── exceptions     # ErrorCode del contexto y fábricas de DomainException
├── application
│   ├── port
│   │   ├── in         # Interfaces de casos de uso (driving)
│   │   └── out        # Interfaces de repositorios/gateways (driven)
│   └── usecase        # Implementaciones de los puertos in
└── infrastructure
    └── adapters
        ├── in
        │   └── rest
        │       ├── dto        # Requests/responses del borde HTTP
        │       └── ...        # Controllers/resources
        └── out
            ├── persistence
            │   ├── entity     # @Entity JPA
            │   ├── mapper     # entity <-> modelo de dominio
            │   └── repository # Implementaciones de los puertos out
            └── <tecnologia>   # Otras familias de adaptadores driven (ej. security, http, messaging)
```

Reglas de dependencia:

- `domain` no importa nada de `application` ni de `infrastructure`.
- `application` sólo conoce `domain`.
- `infrastructure` implementa los puertos; nunca al revés.
- Los adaptadores `in` no hablan con los adaptadores `out` directamente: siempre pasan por un puerto `in`.

### Comunicación entre contextos

- Un contexto sólo depende de otro a través de su **puerto `in`** y su modelo de dominio (ej. `auth` usa `UsersUseCase`).
- Nunca se importa el puerto `out`, la entidad JPA ni el adaptador de otro contexto.
- Sin relaciones JPA entre entidades de contextos distintos: se guarda el `UUID` y la FK la mantiene Liquibase.

### Separación auth / user

- `user` gestiona identidad y estado de la cuenta (`users`, `player_stats`). **No conoce contraseñas ni tokens.**
- `auth` gestiona credenciales y sesión (`user_credential`, hashing bcrypt, emisión de JWT).
- El registro lo orquesta `auth`: valida la contraseña, crea el usuario vía `UsersUseCase` y guarda la credencial.

### Excepción: `org.hexarch.shared`

Es transversal y no es un contexto de negocio. Mantiene su forma actual:

```
org.hexarch.shared
├── domain                 # DomainException, ErrorCode
└── infrastructure.rest    # ApiResponse, ApiError, ApiWraped, filtros, ExceptionMappers
```

## Nomenclatura

| Elemento              | Patrón                        |
| --------------------- | ----------------------------- |
| Entidad JPA           | `XxxEntity`                   |
| Mapper de persistencia| `XxxMapper`                   |
| Puerto out            | `XxxRepositoryPort`           |
| Adaptador out         | `XxxRepositoryAdapter`        |
| Puerto in             | `XxxUseCase`                  |
| Servicio de aplicación| `XxxService`                  |
| Controller REST       | `XxxController`               |
| DTO                   | `XxxDto` / `CreateXxxDto`     |

`@ApiWraped` va **en el método**, no en la clase: `ApiResponseFilter` lo lee del método del recurso.

## Estilo de código

- Comentarios en español, una sola línea, sólo para explicar lo que el código no puede mostrar (el porqué, no el qué). Nada de Javadoc de relleno.
- Nada de Lombok. Records para el dominio y los DTOs.
- Entidades JPA: `extends PanacheEntityBase` con **campos públicos** (estilo Panache), sin getters/setters.
- Imports explícitos, sin comodines. Orden: `java.*`, terceros (`org.*`), `io.quarkus.*`, `jakarta.*`.
- IDs: `UUID` con `@GeneratedValue(strategy = GenerationType.UUID)`.
- Relaciones: `@ManyToOne(fetch = FetchType.LAZY)` siempre; nombrar las FK con `@ForeignKey(name = "fk_<tabla>_<columna>")`.

## Errores

- Todo error de negocio se lanza como `DomainException` (`RuleViolation`, `NotFound`, `Conflict`) con un `ErrorCode` propio del contexto.
- `getMessage()` devuelve el **código**; el texto legible se resuelve en el borde REST según `Accept-Language`.
- Cada código nuevo se registra en `src/main/resources/errors.properties` (base, inglés) y `errors_es.properties`.
- Formato del código: `<CONTEXTO>-<NNN>` (ej. `AUTH-001`, `USER-002`, `LEVEL-001`).
- Los errores de autenticación nunca revelan si la cuenta existe.

## Seguridad

- Contraseñas con bcrypt (`BcryptUtil`), nunca en logs: `RawPassword` enmascara su `toString()`.
- JWT RS256 (algoritmo por defecto de MP-JWT); el `sub` es el id del usuario, no el email.
- En dev/test Quarkus genera el par RSA en memoria porque no hay clave configurada. En `%prod` hay que apuntar `smallrye.jwt.sign.key.location` y `mp.jwt.verify.publickey.location` a los ficheros PEM.
- No usar claves simétricas: Quarkus no cablea `smallrye.jwt.verify.secretkey`, así que HS256 sólo funciona con un JWK y no compensa.
- Endpoints públicos con `@PermitAll`, el resto con `@Authenticated` o `@RequirePermission`.

## Autorización

Dos ejes **ortogonales**. No mezclarlos ni meterlos en el mismo enum.

**1. Plataforma (global).** Depende sólo de quién eres.

- `shared.domain.security.PlatformRole` (`PLAYER`, `MODERATOR`, `ADMIN`) → `Set<PlatformPermission>`. El mapeo vive en código, no en BD.
- El rol se guarda en `users.role` (enum PG `user_role`) y se emite en el claim `groups` del JWT.
- En el endpoint: `@RequirePermission(PlatformPermission.USER_BAN)`. Implica autenticación (401 sin token, 403 sin permiso con `AUTHZ-001`).
- Vive en `shared` porque los permisos cruzan contextos (`LEVEL_APPROVE` lo consume `level`, `USER_BAN` lo consume `user`); ponerlo en un contexto obligaría a que otro lo importara.
- **Consecuencia del claim**: un cambio de rol o un baneo no surte efecto hasta que expira el token (1 h). Para operaciones críticas, revalidar contra BD en el caso de uso.

**2. Recurso (por nivel).** Depende de quién eres **y** de qué nivel: no se puede resolver sólo con el JWT, hay que ir a `level_members`. Se resolverá con `@RequireLevelPermission` + un parámetro `@LevelId` cuando el contexto `level` tenga endpoints.

La anotación es la **primera barrera** del adaptador `in`. Si la regla es de negocio, reafirmarla en el caso de uso a través de un puerto, para que no se escape desde otro adaptador.

### Tres estados de autenticación, no dos

- **Público** (`@PermitAll`), **autenticado** (`@Authenticated` / `@RequirePermission`) y **autenticación opcional**: endpoints públicos que enriquecen la respuesta si viene un token.
- Los casos de uso reciben `shared.domain.security.Caller` como **primer parámetro**, nunca `JsonWebToken`. `Caller` nunca es null: un visitante anónimo es `Caller.ANONYMOUS`.
- El único `if (token != null)` vive en `CallerResolver`, que los controllers invocan con `callerResolver.current()`. Nada de duplicar endpoints en `/me/...`.
- `Caller` no se inyecta: es un `record` y CDI no puede proxiar clases finales, así que un bean `@RequestScoped` fallaría al arrancar.
- Un token inválido o caducado devuelve 401 aunque el endpoint sea `@PermitAll`. Es lo correcto; no desactivar la autenticación proactiva para tragárselo.
- Sobre recursos ajenos no visibles se devuelve **404, no 403**: un 403 confirmaría que ese id existe.

## i18n

- Dos bundles: `errors.properties` (códigos de error) y `messages.properties` (mensajes de éxito), ambos con su `_es`.
- El texto se resuelve en el borde REST según `Accept-Language`; el helper es `shared.infrastructure.rest.Messages`.
- `@ApiWraped(message = "auth.login.success")` recibe una **clave**, no un literal. Si la clave no está traducida se devuelve tal cual.
- `ApiResponseFilter` no envuelve respuestas que no sean 2xx: los errores ya vienen con su forma final de `ExceptionMappers`.

## Configuración

- `application.properties` guarda **valores por defecto de desarrollo**, nunca secretos reales.
- Lo que cambia por entorno va por variable de entorno. Quarkus lee `.env` en la raíz y tiene prioridad sobre `application.properties`.
- `.env.example` es la plantilla versionada; `.env` está en `.gitignore`. Al añadir una variable nueva hay que añadirla también al ejemplo.
- Nombre de la variable = propiedad en MAYÚSCULAS con `_` (`hexarch.jwt.issuer` → `HEXARCH_JWT_ISSUER`).
- `docker-compose.yml` lee el mismo `.env`, así que las credenciales de Postgres se declaran una sola vez.
- Config específica de producción con el prefijo `%prod.` en vez de un fichero aparte.

## Base de datos y Liquibase

- `src/main/resources/db/changeLog.xml` es sólo el maestro: contiene `<include>`, nunca changesets.
- Los changesets viven en `src/main/resources/db/changelog/NNN-descripcion.xml`.
- Un changeset ya aplicado **no se edita**: se añade uno nuevo.
- `author="hexarch"`, `id="<archivo>-<n>-<descripcion>"`.
- Nombres SQL en `snake_case`; tablas en plural.
- Enums nativos de PostgreSQL: las etiquetas van en **MAYÚSCULA** porque Hibernate mapea por el nombre de la constante Java. En la entidad se usa `@Enumerated(EnumType.STRING)` + `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` + `columnDefinition = "<tipo_pg>"`.
- `byte[]` sin `@Lob` y con `columnDefinition = "bytea"` (con `@Lob` PostgreSQL lo mapearía a OID).
- El dump de referencia del esquema está en `db/sql-dump.hexi-dash.sql` (fuera de `src/`).

## Reglas de trabajo

- Las peticiones HTTP de prueba viven en `api/<contexto>.http`, un archivo por bounded context. Al añadir un endpoint se añade su petición ahí (incluyendo los casos de error).
- No crear archivos Markdown de documentación salvo petición explícita.
- Antes de dar por terminada una tarea: `./mvnw.cmd -q clean compile`.
- Si cambia el esquema, actualizar entidad **y** changelog en el mismo cambio.
