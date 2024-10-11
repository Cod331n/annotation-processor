package ru.cod331n.util.cache;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс для кэша, который сопоставляет ключи с соответствующими значениями.
 *
 * <p>Кэш используется для хранения данных в виде пар "ключ-значение", с возможностью быстрого
 * доступа к значению по ключу.</p>
 *
 * @param <K> Тип ключей, используемых для поиска значений.
 * @param <V> Тип значений, хранимых в кэше.
 */
public interface Cache<K, V> {

    /**
     * Возвращает значение, связанное с указанным ключом, если оно присутствует в кэше.
     *
     * @param key Ключ для поиска значения. Не может быть {@code null}.
     * @return Значение, связанное с данным ключом, или {@code null}, если оно отсутствует.
     */
    @Nullable
    @Contract(pure = true)
    V get(@NotNull K key);

    /**
     * Добавляет или обновляет значение, связанное с указанным ключом в кэше.
     *
     * @param key   Ключ, с которым связано новое значение. Не может быть {@code null}.
     * @param value Новое значение для сохранения. Не может быть {@code null}.
     */
    @Contract(mutates = "this")
    void put(@NotNull K key, @NotNull V value);

    /**
     * Проверяет, содержится ли в кэше указанное значение ключа.
     *
     * @param key Ключ для проверки наличия. Не может быть {@code null}.
     * @return {@code true}, если ключ содержится в кэше, иначе {@code false}.
     */
    @Contract(pure = true)
    boolean containsKey(@NotNull K key);
}
