package ru.cod331n.annotation.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.util.tuple.Pair;
import ru.cod331n.util.validation.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public final class JavaClassesReflection {
    private final ClassLoaderHelper classLoaderHelper;

    private final int i = 1;

    public JavaClassesReflection(final @NotNull String packageName, final @Nullable ClassLoader classLoader) {
        this.classLoaderHelper = new ClassLoaderHelper(packageName, classLoader);
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Class<?>> getPackageClasses() {
        return classLoaderHelper.getPackageClasses();
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Class<?>> getAnnotatedClasses(@NotNull Class<? extends Annotation> annotation) {
        return getPackageClasses().stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, Method>> getAnnotatedMethods(@NotNull Class<? extends Annotation> annotation) {
        return collectAnnotatedElements(
                clazz -> Arrays.stream(clazz.getDeclaredMethods()),
                Method::isAnnotationPresent,
                annotation
        );
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, Field>> getAnnotatedFields(@NotNull Class<? extends Annotation> annotation) {
        return collectAnnotatedElements(
                clazz -> Arrays.stream(clazz.getDeclaredFields()),
                Field::isAnnotationPresent,
                annotation
        );
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, Constructor<?>>> getAnnotatedConstructors(@NotNull Class<? extends Annotation> annotation) {
        return collectAnnotatedElements(
                clazz -> Arrays.stream(clazz.getDeclaredConstructors()),
                Constructor::isAnnotationPresent,
                annotation
        );
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, Parameter>> getAnnotatedParameters(@NotNull Class<? extends Annotation> annotation) {
        Collection<Pair<Class<?>, Parameter>> global = new LinkedList<>();

        getPackageClasses().forEach(clazz -> {
            for (Method method : clazz.getDeclaredMethods()) {
                for (Parameter parameter : method.getParameters()) {
                    Preconditions.check(
                            parameter.isAnnotationPresent(annotation),
                            () -> global.add(new Pair<>(clazz, parameter))
                    );
                }
            }

            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                for (Parameter parameter : constructor.getParameters()) {
                    Preconditions.check(
                            parameter.isAnnotationPresent(annotation),
                            () -> global.add(new Pair<>(clazz, parameter))
                    );
                }
            }
        });

        return global;
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Package> getAnnotatedPackages(@NotNull Class<? extends Annotation> annotation) {
        return Arrays.stream(Package.getPackages())
                .filter(pkg -> pkg.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, AnnotatedType>> getAnnotatedTypesUse(@NotNull Class<? extends Annotation> annotation) {
        Collection<Pair<Class<?>, AnnotatedType>> global = new LinkedList<>();

        getPackageClasses().forEach(clazz -> {
            collectAnnotatedElementsByClass(Stream.of(clazz.getAnnotatedSuperclass()), annotation, clazz, global);
            collectAnnotatedElementsByClass(Arrays.stream(clazz.getDeclaredFields()).map(Field::getAnnotatedType), annotation, clazz, global);
            collectAnnotatedElementsByClass(Arrays.stream(clazz.getDeclaredMethods()).map(Method::getAnnotatedReturnType), annotation, clazz, global);
            collectAnnotatedElementsByClass(Arrays.stream(clazz.getDeclaredConstructors()).flatMap(constructor -> Arrays.stream(constructor.getAnnotatedParameterTypes())), annotation, clazz, global);
        });

        return global;
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, TypeVariable<?>>> getAnnotatedTypeParameters(@NotNull Class<? extends Annotation> annotation) {
        Collection<Pair<Class<?>, TypeVariable<?>>> global = new LinkedList<>();

        getPackageClasses().forEach(clazz -> {
            collectAnnotatedElementsByClass(Arrays.stream(clazz.getTypeParameters()), annotation, clazz, global);

            Arrays.stream(clazz.getDeclaredMethods())
                    .forEach(method -> collectAnnotatedElementsByClass(Arrays.stream(method.getTypeParameters()), annotation, clazz, global));

            Arrays.stream(clazz.getDeclaredConstructors())
                    .forEach(constructor -> collectAnnotatedElementsByClass(Arrays.stream(constructor.getTypeParameters()), annotation, clazz, global));
        });

        return global;
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Pair<Class<?>, Class<?>>> getAnnotatedAnnotationTypes(@NotNull Class<? extends Annotation> annotation) {
        Collection<Pair<Class<?>, Class<?>>> global = new LinkedList<>();

        getPackageClasses().stream()
                .filter(Class::isAnnotation)
                .filter(annotatedClass -> annotatedClass.isAnnotationPresent(annotation))
                .forEach(annotatedClass -> global.add(new Pair<>(annotatedClass, annotatedClass)));

        return global;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Contract(pure = true)
    public <T> T getGenericType(@NotNull Class<?> clazz) {
        Type genericSuperclass;

        try {
            genericSuperclass = clazz.getGenericSuperclass();
        } catch (TypeNotPresentException e) {
            return null;
        }

        return Preconditions.checkAndReturn(
                genericSuperclass instanceof ParameterizedType,
                () -> {
                    ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();

                    return Preconditions.checkAndReturn(
                            typeArguments.length > 0 && typeArguments[0] instanceof Class<?>,
                            () -> (T) typeArguments[0],
                            () -> null
                    );
                },
                () -> null
                );
    }

    @NotNull
    @Contract(pure = true)
    private <T extends AnnotatedElement> Collection<Pair<Class<?>, T>> collectAnnotatedElements(
            @NotNull Function<Class<?>, Stream<T>> elementSupplier,
            @NotNull BiPredicate<T, Class<? extends Annotation>> annotationChecker,
            @NotNull Class<? extends Annotation> annotation
    ) {
        Collection<Pair<Class<?>, T>> global = new LinkedList<>();

        getPackageClasses().forEach(clazz -> collectAnnotatedElementsByClass(
                elementSupplier.apply(clazz),
                annotationChecker,
                annotation,
                clazz,
                global)
        );

        return global;
    }

    @Contract(mutates = "param5")
    private <T extends AnnotatedElement> void collectAnnotatedElementsByClass(
            @NotNull Stream<T> elementsStream,
            @NotNull BiPredicate<T, Class<? extends Annotation>> annotationChecker,
            @NotNull Class<? extends Annotation> annotation,
            @NotNull Class<?> clazz,
            @NotNull Collection<Pair<Class<?>, T>> collection
    ) {
        elementsStream
                .filter(element -> annotationChecker.test(element, annotation))
                .forEach(element -> collection.add(new Pair<>(clazz, element)));
    }

    @Contract(mutates = "param4")
    private <T extends AnnotatedElement> void collectAnnotatedElementsByClass(
            @NotNull Stream<T> elementsStream,
            @NotNull Class<? extends Annotation> annotation,
            @NotNull Class<?> clazz,
            @NotNull Collection<Pair<Class<?>, T>> collection
    ) {
        elementsStream
                .filter(element -> element.isAnnotationPresent(annotation))
                .forEach(element -> collection.add(new Pair<>(clazz, element)));
    }
}
