package ru.cod331n.annotation.processor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

// SUPPORTS ONLY RUNTIME ANNOTATIONS BECAUSE OF REFLECTIONS
public abstract class AbstractAnnotationProcessor<T extends Annotation> {
    @Contract(pure = true)
    public abstract void process(@Nullable Class<?> clazz, @NotNull AnnotatedElement element, @NotNull T annotation);
}