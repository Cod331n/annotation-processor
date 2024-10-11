package ru.cod331n.util.tuple;

import org.jetbrains.annotations.Nullable;

public class ImmutablePair<L, R> {
    private final L left;
    private final R right;

    public ImmutablePair(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
