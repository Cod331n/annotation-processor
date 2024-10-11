package ru.cod331n.util.cache;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CacheHolder implements Cache<String, Collection<Class<?>>> {
    private final Map<String, Collection<Class<?>>> cache = new HashMap<>();

    @Override
    @Contract(pure = true)
    public @Nullable Collection<Class<?>> get(@NotNull String key) {
        return cache.get(key);
    }

    @Override
    @Contract(pure = false)
    public void put(@NotNull String key, @NotNull Collection<Class<?>> value) {
        this.cache.put(key, value);
    }

    @Override
    @Contract(pure = true)
    public boolean containsKey(@NotNull String key) {
        return cache.containsKey(key);
    }
}
