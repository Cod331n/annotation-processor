package ru.cod331n.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, которой должны помечаться все процессоры аннотаций, наследуемые
 * от класса {@link ru.cod331n.annotation.processor.AbstractAnnotationProcessor}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationProcessor {
}
