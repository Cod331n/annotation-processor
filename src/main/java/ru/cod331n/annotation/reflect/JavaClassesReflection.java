package ru.cod331n.annotation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.cache.Cache;
import ru.cod331n.cache.CacheObject;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public final class JavaClassesUtil {
    public static final String CLASS_FILE_NAME_EXTENSION = ".class";

    private final ClassLoader classLoader;
    private final String packageName;
    private final URL packageUrl;
    private final Cache<String, Collection<Class<?>>> packageCache;

    public JavaClassesUtil(final @NotNull String packageName, final @Nullable ClassLoader classLoader) {
        this.classLoader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
        this.packageName = packageName.replaceAll("/", ".");
        this.packageUrl = this.classLoader.getResource(packageName.replaceAll("\\.", "/"));
        this.packageCache = new CacheObject();
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Class<?>> getPackageClasses() {
        if (packageCache.containsKey(packageName)) {
            return packageCache.get(packageName);
        }

        Collection<Class<?>> classes = getPackageClasses(new File(packageUrl.getPath()), packageName);
        packageCache.put(packageName, classes);
        return classes;
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
        return getPackageClasses().stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Method> getAnnotatedMethods(Class<? extends Annotation> annotation) {
        return getPackageClasses().stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Field> getAnnotatedFields(Class<? extends Annotation> annotation) {
        return getPackageClasses().stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredFields()))
                .filter(field -> field.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Constructor<?>> getAnnotatedConstructors(Class<? extends Annotation> annotation) {
        return getPackageClasses().stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredConstructors()))
                .filter(constructor -> constructor.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Parameter> getAnnotatedParameters(Class<? extends Annotation> annotation) {
        Collection<Parameter> annotatedParameters = new LinkedList<>();

        getPackageClasses().forEach(clazz -> {
            for (Method method : clazz.getDeclaredMethods()) {
                for (Parameter parameter : method.getParameters()) {
                    if (parameter.isAnnotationPresent(annotation)) {
                        annotatedParameters.add(parameter);
                    }
                }
            }

            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                for (Parameter parameter : constructor.getParameters()) {
                    if (parameter.isAnnotationPresent(annotation)) {
                        annotatedParameters.add(parameter);
                    }
                }
            }
        });

        return annotatedParameters;
    }

    @NotNull
    @Contract(pure = true)
    private Collection<Class<?>> getPackageClasses(final @NotNull File directory, final @NotNull String packageName) {
        Collection<Class<?>> collection = new LinkedList<>();

        if (!directory.isDirectory() || directory.listFiles() == null) {
            return collection;
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            String fileName = file.getName();

            if (file.isDirectory()) {
                String subPackageName = packageName + '.' + fileName;
                collection.addAll(getPackageClasses(file, subPackageName));
            } else if (fileName.endsWith(CLASS_FILE_NAME_EXTENSION)) {
                String className = packageName + '.' + fileName.substring(0, fileName.length() - CLASS_FILE_NAME_EXTENSION.length());

                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    collection.add(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found: " + className, e);
                }
            }
        }

        return collection;
    }
}
