package ru.cod331n.annotation.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.annotation.AnnotationProcessor;
import ru.cod331n.annotation.Validate;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

@AnnotationProcessor
public class TestProcessor extends AbstractAnnotationProcessor<Validate> {
    @Override
    public void process(@Nullable Class<?> clazz, @NotNull AnnotatedElement element, @NotNull Validate annotation) {
        Field field = (Field) element;
        field.setAccessible(true);

    }
}
