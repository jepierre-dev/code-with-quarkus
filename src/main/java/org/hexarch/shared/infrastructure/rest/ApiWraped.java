package org.hexarch.shared.infrastructure.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.ws.rs.NameBinding;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ApiWraped {

    /** Clave del bundle messages; si no hay traduccion se devuelve la clave tal cual. */
    String message() default "response.success";

    boolean includeStatus() default true;

}
