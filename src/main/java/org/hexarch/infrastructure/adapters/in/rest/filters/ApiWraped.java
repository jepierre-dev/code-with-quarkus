package org.hexarch.infrastructure.adapters.in.rest.filters;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.ElementType;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ApiWraped {

    String message() default "API response wrapped successfully.";

    boolean includeStatus() default true;
    
}
