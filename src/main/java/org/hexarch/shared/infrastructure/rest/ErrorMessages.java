package org.hexarch.shared.infrastructure.rest;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.ws.rs.core.HttpHeaders;

/** Resuelve el texto de un codigo de error segun el Accept-Language de la peticion. */
public final class ErrorMessages {

    private static final String BUNDLE = "errors";

    // Sin este control, un locale desconocido caeria en el locale por defecto de la JVM en vez de en errors.properties.
    private static final ResourceBundle.Control NO_JVM_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private ErrorMessages() {
    }

    public static ResourceBundle bundleFor(HttpHeaders headers) {
        // El classloader del caller no siempre ve los recursos de la app; el de contexto si.
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        for (Locale locale : headers.getAcceptableLanguages()) {
            String language = locale.getLanguage();
            if (language.isEmpty() || "*".equals(language)) {
                continue;
            }
            try {
                return ResourceBundle.getBundle(BUNDLE, locale, loader, NO_JVM_FALLBACK);
            } catch (MissingResourceException e) {
                // idioma sin bundle: prueba el siguiente de la lista de preferencia
            }
        }
        return ResourceBundle.getBundle(BUNDLE, Locale.ROOT, loader, NO_JVM_FALLBACK);
    }

    public static String resolve(ResourceBundle bundle, String code, Map<String, Object> params) {
        if (!bundle.containsKey(code)) {
            return code;
        }

        String message = bundle.getString(code);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            message = message.replace("{" + param.getKey() + "}", String.valueOf(param.getValue()));
        }
        return message;
    }

    /** El bundle base no tiene locale, pero su contenido esta en ingles. */
    public static String languageTagOf(ResourceBundle bundle) {
        Locale locale = bundle.getLocale();
        return locale.getLanguage().isEmpty() ? "en" : locale.toLanguageTag();
    }
}
