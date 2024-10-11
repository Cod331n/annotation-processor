package ru.cod331n.util.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Утилитарный класс для проверки условий.
 *
 * <p>Этот класс предоставляет набор статических методов для валидации условий
 * и выполнения определенных действий в зависимости от результата проверки.
 * Он не может быть инициализирован.</p>
 */
public final class Preconditions {

    private Preconditions() {
        throw new AssertionError("Utility class couldn't be initialized");
    }

    /**
     * Проверяет условие и выполняет действие, если оно истинно.
     *
     * @param condition Условие для проверки.
     * @param handle    Действие, которое нужно выполнить, если условие истинно.
     */
    public static void check(boolean condition, @NotNull Runnable handle) {
        if (condition) {
            handle.run();
        }
    }

    /**
     * Выполняет одно действие, если условие истинно, и другое, если условие ложно.
     *
     * @param condition условие, которое проверяется
     * @param handle    действие, выполняемое, если условие истинно
     * @param orElse    действие, выполняемое, если условие ложно
     */
    public static void checkOrElse(boolean condition, @NotNull Runnable handle, @NotNull Runnable orElse) {
        if (condition) {
            handle.run();
        } else {
            orElse.run();
        }
    }

    /**
     * Проверяет условие и бросает исключение, если оно истинно.
     *
     * @param condition         Условие для проверки.
     * @param exceptionSupplier Функция, которая возвращает исключение.
     */
    public static <E extends Throwable> void checkAndThrow(boolean condition, @NotNull Supplier<E> exceptionSupplier) throws E {
        if (condition) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Проверяет условие и возвращает результат функции, если условие истинно.
     *
     * @param condition Условие для проверки.
     * @param handle    Функция, которая будет вызвана при истинном условии.
     * @param orElse    Функция, которая будет вызвана при ложном условии.
     * @param <T>       Тип возвращаемого значения.
     * @return Результат функции или {@code null}, если условие ложно.
     */
    @Nullable
    public static <T> T checkAndReturn(boolean condition, @NotNull Supplier<T> handle, @NotNull Supplier<T> orElse) {
        return condition ? handle.get() : orElse.get();
    }
}
