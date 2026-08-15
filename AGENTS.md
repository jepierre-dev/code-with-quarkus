# AGENTS.md

Convenciones obligatorias de este repositorio. Leer antes de crear o mover cualquier archivo.

## Stack

- Quarkus 3.38.2, Java 21, Maven wrapper (`./mvnw.cmd` en Windows).
- Hibernate ORM con Panache, PostgreSQL, Liquibase (`quarkus.liquibase.migrate-at-start=true`).
- Build/verificación rápida: `./mvnw.cmd -q clean compile`.

## Arquitectura hexagonal — encarpetado canónico

Un paquete por bounded context bajo `org.hexarch.<contexto>` (ej. `user`, `level`).
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
            └── persistence
                ├── entity     # @Entity JPA
                ├── mapper     # entity <-> modelo de dominio
                └── repository # Implementaciones de los puertos out
```

Reglas de dependencia:

- `domain` no importa nada de `application` ni de `infrastructure`.
- `application` sólo conoce `domain`.
- `infrastructure` implementa los puertos; nunca al revés.
- Los adaptadores `in` no hablan con los adaptadores `out` directamente: siempre pasan por un puerto `in`.

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

## Estilo de código

- Comentarios en español, una sola línea, sólo para explicar lo que el código no puede mostrar (el porqué, no el qué). Nada de Javadoc de relleno.
- Nada de Lombok. Records para el dominio y los DTOs.
- Entidades JPA: `extends PanacheEntityBase` con **campos públicos** (estilo Panache), sin getters/setters.
- Imports explícitos, sin comodines. Orden: `java.*`, terceros (`org.*`), `io.quarkus.*`, `jakarta.*`.
- IDs: `UUID` con `@GeneratedValue(strategy = GenerationType.UUID)`.
- Relaciones: `@ManyToOne(fetch = FetchType.LAZY)` siempre; nombrar las FK con `@ForeignKey(name = "fk_<tabla>_<columna>")`.

## Errores

- Todo error de negocio se lanza como `DomainException` (`RuleViolation`, `NotFound`, ...) con un `ErrorCode` propio del contexto.
- `getMessage()` devuelve el **código**; el texto legible se resuelve en el borde REST según `Accept-Language`.
- Cada código nuevo se registra en `src/main/resources/errors.properties` (base, inglés) y `errors_es.properties`.
- Formato del código: `<CONTEXTO>-<NNN>` (ej. `LEVEL-001`).

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

- No crear archivos Markdown de documentación salvo petición explícita.
- Antes de dar por terminada una tarea: `./mvnw.cmd -q clean compile`.
- Si cambia el esquema, actualizar entidad **y** changelog en el mismo cambio.
