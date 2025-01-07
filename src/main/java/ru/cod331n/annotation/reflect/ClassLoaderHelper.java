package ru.cod331n.annotation.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.util.cache.Cache;
import ru.cod331n.util.cache.CacheHolder;
import ru.cod331n.util.validation.Preconditions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.jar.JarFile;

/**
 * Утилитарный класс для загрузки классов из заданного пакета.
 *
 * <p> Этот класс позволяет загружать все классы из указанного пакета, используя
 * класс загрузчика. Также кэширует результаты для повышения производительности. </p>
 */
public final class ClassLoaderHelper {
    public static final String CLASS_FILE_NAME_EXTENSION = ".class";

    private final ClassLoader classLoader;
    private final String packageName;
    private final URL packageUrl;
    private final Cache<String, Collection<Class<?>>> packageCache;

    public ClassLoaderHelper(final @NotNull String packageName, final @Nullable ClassLoader classLoader) {
        this.classLoader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;

        this.packageName = packageName.replaceAll("/", ".");
        this.packageUrl = this.classLoader.getResource(packageName.replaceAll("\\.", "/"));
        this.packageCache = new CacheHolder();

        Preconditions.checkAndThrow(
                packageUrl == null,
                () -> new IllegalArgumentException("Package not found or empty: " + packageName)
        );
    }

    @NotNull
    @Contract(pure = true)
    public Collection<Class<?>> getPackageClasses() {
        try {
            switch (packageUrl.getProtocol()) {
                case "file":
                    Collection<Class<?>> classes = getPackageClasses(new File(packageUrl.getPath()), packageName);
                    packageCache.put(packageName, classes);
                    return classes;
                case "jar":
                    return getPackageClassesFromJar(packageUrl, packageName);
                default:
                    throw new IllegalArgumentException("Unsupported protocol: " + packageUrl.getProtocol());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load classes from package: " + packageName, e);
        }
    }

    @NotNull
    @Contract(pure = true)
    private Collection<Class<?>> getPackageClassesFromJar(@NotNull URL resource, @NotNull String packageName) throws IOException {
        Collection<Class<?>> classes = new ArrayList<>();
        String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));

        try (JarFile jarFile = new JarFile(jarPath)) {
            jarFile.stream().filter(entry -> entry.getName().startsWith(packageName.replace(".", "/")))
                    .forEach(entry -> {
                        if (entry.getName().endsWith(CLASS_FILE_NAME_EXTENSION)) {
                            String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - CLASS_FILE_NAME_EXTENSION.length());
                            try {
                                classes.add(classLoader.loadClass(className));
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException("Class not found in JAR file: " + className, e);
                            }
                        }
                    });

            return classes;
        }
    }

    @NotNull
    @Contract(pure = true)
    private Collection<Class<?>> getPackageClasses(@NotNull File directory, @NotNull String packageName) {
        Collection<Class<?>> collection = new LinkedList<>();

        Preconditions.checkAndThrow(
                !directory.isDirectory(),
                () -> new IllegalArgumentException("Provided path is not a directory: " + directory.getPath())
        );

        File[] files = directory.listFiles();
        Preconditions.checkAndThrow(
                files == null,
                () -> new IllegalArgumentException("Unable to list files in directory: " + directory.getPath())
        );

        for (File file : files) {
            String fileName = file.getName();

            Preconditions.check(
                    file.isDirectory(),
                    () -> {
                        String subPackageName = packageName + '.' + fileName;
                        collection.addAll(getPackageClasses(file, subPackageName));
                    }
            );

            Preconditions.check(
                    fileName.endsWith(CLASS_FILE_NAME_EXTENSION),
                    () -> {
                        String className = packageName + '.' + fileName.substring(0, fileName.length() - CLASS_FILE_NAME_EXTENSION.length());
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            collection.add(clazz);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Class not found: " + className, e);
                        }
                    }
            );
        }

        return collection;
    }
}
