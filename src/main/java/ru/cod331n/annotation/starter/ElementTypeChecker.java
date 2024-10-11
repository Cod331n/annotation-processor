package ru.cod331n.annotation.starter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.annotation.reflect.JavaClassesReflection;
import ru.cod331n.util.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ElementTypeChecker {
    private final Map<ElementType, Function<Class<? extends Annotation>, Collection<? extends Pair<Class<?>, ? extends AnnotatedElement>>>> elementHandlers;

    protected ElementTypeChecker(final @NotNull JavaClassesReflection reflection) {
        elementHandlers = new HashMap<>();

        elementHandlers.put(ElementType.TYPE, ann -> reflection.getAnnotatedClasses(ann).stream()
                .map(cls -> new Pair<Class<?>, AnnotatedElement>(cls, cls))
                .collect(Collectors.toList()));
        elementHandlers.put(ElementType.FIELD, reflection::getAnnotatedFields);
        elementHandlers.put(ElementType.METHOD, reflection::getAnnotatedMethods);
        elementHandlers.put(ElementType.PACKAGE, ann -> reflection.getAnnotatedPackages(ann).stream()
                .map(pkg -> new Pair<Class<?>, AnnotatedElement>(null, pkg))
                .collect(Collectors.toList()));
        elementHandlers.put(ElementType.PARAMETER, reflection::getAnnotatedParameters);
        elementHandlers.put(ElementType.CONSTRUCTOR, reflection::getAnnotatedConstructors);
        elementHandlers.put(ElementType.TYPE_USE, reflection::getAnnotatedTypesUse);
        elementHandlers.put(ElementType.TYPE_PARAMETER, reflection::getAnnotatedTypeParameters);
        elementHandlers.put(ElementType.ANNOTATION_TYPE, reflection::getAnnotatedAnnotationTypes);
    }


    @NotNull
    @Contract(pure = true)
    protected Collection<Pair<Class<?>, ? extends AnnotatedElement>> check(@NotNull Class<? extends Annotation> annotation) {
        Collection<Pair<Class<?>, ? extends AnnotatedElement>> collection = new LinkedList<>();

        Arrays.stream(annotation.getAnnotation(Target.class).value()).forEach(elementType -> {
            @Nullable
            Function<Class<? extends Annotation>, Collection<? extends Pair<Class<?>, ? extends AnnotatedElement>>> handler = elementHandlers.get(elementType);

            if (handler != null) {
                collection.addAll(handler.apply(annotation));
            }
        });

        return collection;
    }
}
