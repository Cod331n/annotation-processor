package ru.cod331n.util.tuple;

import org.jetbrains.annotations.Nullable;

public class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(@Nullable L left, @Nullable R right) {
        this.left = left;
        this.right = right;
    }

    @Nullable
    public L getLeft() {
        return left;
    }

    @Nullable
    public R getRight() {
        return right;
    }
}
