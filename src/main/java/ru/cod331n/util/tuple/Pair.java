package ru.cod331n.util.tuple;

import org.jetbrains.annotations.Nullable;

/**
 * Класс для представления пары значений двух типов.
 *
 * <p>Пара значений может быть использована для хранения и передачи связанных между собой данных.
 * Каждый элемент пары может быть любого типа и допускает значение {@code null}.</p>
 *
 * @param <L> Тип левого элемента пары.
 * @param <R> Тип правого элемента пары.
 */
public class Pair<L, R> {
    private final L left;
    private final R right;

    /**
     * Создает экземпляр {@code Pair} с указанными значениями.
     *
     * @param left  Левый элемент пары. Может быть {@code null}.
     * @param right Правый элемент пары. Может быть {@code null}.
     */
    public Pair(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Возвращает левый элемент пары.
     *
     * @return Левый элемент пары, или {@code null}, если он отсутствует.
     */
    @Nullable
    public L getLeft() {
        return left;
    }

    /**
     * Возвращает правый элемент пары.
     *
     * @return Правый элемент пары, или {@code null}, если он отсутствует.
     */
    @Nullable
    public R getRight() {
        return right;
    }
}