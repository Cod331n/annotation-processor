package ru.cod331n.annotation.processor;

import org.jetbrains.annotations.Contract;

import java.lang.annotation.Annotation;

public interface AnnotationProcessor<T extends Annotation> {
    @Contract(pure = true)
    void process(Class<T> annotation);
}