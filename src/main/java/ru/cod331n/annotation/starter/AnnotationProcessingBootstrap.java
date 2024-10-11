package ru.cod331n.annotation.starter;

import org.jetbrains.annotations.Nullable;
import ru.cod331n.annotation.AnnotationProcessor;
import ru.cod331n.annotation.processor.AbstractAnnotationProcessor;
import ru.cod331n.annotation.reflect.JavaClassesReflection;

import java.lang.annotation.Annotation;

public final class AnnotationBootstrap {
    public static void run(String packageName) {
        JavaClassesReflection reflection = new JavaClassesReflection(packageName, null);

        reflection.getAnnotatedClasses(AnnotationProcessor.class).forEach(clazz -> {
            try {
                Object processorInstance = clazz.getDeclaredConstructor().newInstance();

                if (processorInstance instanceof AbstractAnnotationProcessor) {
                    @Nullable Object annotationType = reflection.getGenericType(clazz);

                    if (annotationType != null) {
                        AbstractAnnotationProcessor<? extends Annotation> processor = (AbstractAnnotationProcessor<? extends Annotation>) processorInstance;

                        processor.process(reflection.getGenericType(clazz));
                    } else {
                        throw new RuntimeException("Unable to determine generic type for processor: " + clazz.getName());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to process annotations for class: " + clazz.getName(), e);
            }
        });
    }
}
