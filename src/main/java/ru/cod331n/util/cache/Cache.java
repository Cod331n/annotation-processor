package ru.cod331n.util.cache;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Cache<K, V> {
    @Nullable
    @Contract(pure = true)
    V get(@NotNull K key);

    @Contract(mutates = "this")
    void put(@NotNull K key, @NotNull V value);

    @Contract(pure = true)
    boolean containsKey(@NotNull K key);
}
