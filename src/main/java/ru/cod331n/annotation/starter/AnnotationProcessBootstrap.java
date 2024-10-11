package ru.cod331n.annotation.starter;

import org.jetbrains.annotations.NotNull;
import ru.cod331n.annotation.starter.logic.AnnotationBootstrapLogic;
import ru.cod331n.util.validation.Preconditions;

/**
 * Класс для инициализации и запуска обработки аннотаций.
 * <p>
 * Этот класс предназначен для использования в качестве точки входа в систему обработки аннотаций.
 * Он содержит статические методы для запуска логики обработки аннотаций в указанном пакете.
 * </p>
 */
public final class AnnotationProcessBootstrap {

    private static final AnnotationBootstrapLogic logic;

    static {
        logic = new AnnotationBootstrapLogic();
    }

    private AnnotationProcessBootstrap() {
        throw new AssertionError("Starter class couldn't be initialized");
    }

    /**
     * Запускает процесс обработки аннотаций в указанном пакете.
     *
     * @param packageName Имя пакета, в котором будет выполнена обработка аннотаций.
     * @throws IllegalArgumentException если переданное имя пакета пустое.
     */
    public static void run(@NotNull String packageName) {
        Preconditions.checkAndThrow(packageName.isEmpty(), () -> new IllegalArgumentException("Package name cannot be empty."));

        logic.run(packageName);
    }
}