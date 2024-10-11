package ru.cod331n.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cod331n.annotation.starter.ElementTypeChecker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CacheHolder implements Cache<String, Collection<Class<?>>> {
    private final Map<String, Collection<Class<?>>> cache = new HashMap<>();

    @Override
    public @Nullable Collection<Class<?>> get(@NotNull String key) {
        return cache.get(key);
    }

    @Override
    public void put(@NotNull String key, @NotNull Collection<Class<?>> value) {
        this.cache.put(key, value);
    }

    @Override
    public boolean containsKey(@NotNull String key) {
        return cache.containsKey(key);
    }
}
