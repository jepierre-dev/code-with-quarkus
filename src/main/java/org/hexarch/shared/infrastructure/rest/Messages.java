package org.hexarch.shared.infrastructure.rest;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Resuelve textos localizados segun el Accept-Language de la peticion. */
public final class Messages {

    public static final String ERRORS = "errors";
    public static final String MESSAGES = "messages";

    // Sin este control, un locale desconocido caeria en el locale por defecto de la JVM en vez del bundle base.
    private static final ResourceBundle.Control NO_JVM_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private Messages() {
    }

    public static ResourceBundle bundleFor(String baseName, List<Locale> acceptableLanguages) {
        // El classloader del caller no siempre ve los recursos de la app; el de contexto si.
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        for (Locale locale : acceptableLanguages) {
            String language = locale.getLanguage();
            if (language.isEmpty() || "*".equals(language)) {
                continue;
            }
            try {
                return ResourceBundle.getBundle(baseName, locale, loader, NO_JVM_FALLBACK);
            } catch (MissingResourceException e) {
                // idioma sin bundle: prueba el siguiente de la lista de preferencia
            }
        }
        return ResourceBundle.getBundle(baseName, Locale.ROOT, loader, NO_JVM_FALLBACK);
    }

    /** Devuelve la clave tal cual si no esta traducida, para no ocultar el codigo al cliente. */
    public static String resolve(ResourceBundle bundle, String key, Map<String, Object> params) {
        if (!bundle.containsKey(key)) {
            return key;
        }

        String message = bundle.getString(key);
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
