package ru.cod331n.annotation.starter.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.annotation.AnnotationProcessor;
import ru.cod331n.annotation.processor.AbstractAnnotationProcessor;
import ru.cod331n.annotation.reflect.JavaClassesReflection;
import ru.cod331n.util.tuple.Pair;
import ru.cod331n.util.validation.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

public final class AnnotationBootstrapLogic {
    public void run(@NotNull String packageName, @Nullable ClassLoader classLoader) {
        final JavaClassesReflection reflection = new JavaClassesReflection(packageName, classLoader);

        reflection.getAnnotatedClasses(AnnotationProcessor.class).forEach(clazz -> {
            try {
                Object processorInstance = clazz.getDeclaredConstructor().newInstance();
                Class<? extends Annotation> annotationProcessing = reflection.getGenericType(clazz);

                Preconditions.checkAndThrow(
                        !(processorInstance instanceof AbstractAnnotationProcessor) || annotationProcessing == null,
                        () -> new RuntimeException("Unable to determine generic type for processor: " + clazz.getName())
                );

                AbstractAnnotationProcessor<? extends Annotation> processor = (AbstractAnnotationProcessor<? extends Annotation>) processorInstance;

                ElementTypeChecker elementTypeChecker = new ElementTypeChecker(reflection);
                Collection<Pair<Class<?>, ? extends AnnotatedElement>> annotatedElements = elementTypeChecker.check(annotationProcessing);

                annotatedElements.forEach(pair -> processor.process(
                        pair.getLeft(),
                        pair.getRight(),
                        pair.getRight().getAnnotation(reflection.getGenericType(clazz)))
                );

            } catch (Exception e) {
                throw new RuntimeException("Failed to process annotations for class: " + clazz.getName(), e);
            }
        });
    }
}
