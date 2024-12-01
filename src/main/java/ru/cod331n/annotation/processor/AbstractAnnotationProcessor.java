package ru.cod331n.annotation.processor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Абстрактный класс для создания процессоров аннотаций, поддерживающий только аннотации с временем
 * жизни {@code RUNTIME}, так как использует рефлексию для обработки.
 *
 * <p>Этот класс предназначен для создания процессоров аннотаций, которые могут обрабатывать аннотации.</p>
 *
 * @param <T> Тип аннотации, которую обрабатывает процессор.
 * @apiNote Каждый наследуемый класс должен быть помечен аннотацией {@link ru.cod331n.annotation.AnnotationProcessor},
 * чтобы стартер его увидел.
 */
public abstract class AbstractAnnotationProcessor<T extends Annotation> {

    /**
     * Обрабатывает аннотацию, примененную к указанному классу или элементу.
     *
     * @param clazz      Класс, к которому принадлежит элемент с аннотацией, может быть {@code null}.
     * @param element    Элемент, к которому применяется аннотация.
     * @param annotation Аннотация, которую необходимо обработать.
     */
    @Contract(pure = true)
    public abstract void process(@Nullable Class<?> clazz, @NotNull AnnotatedElement element, @NotNull T annotation);
}