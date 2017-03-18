/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.data;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.value.BaseValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DataQueryable<K> extends DataView<K> {

    /**
     * Parses a {@link String} into a key type.
     *
     * @param key A {@link String} representation of a key
     * @return A key
     */
    K key(String key);

    /**
     * Returns whether this {@link DataView} contains the given path.
     *
     * @param path The path relative to this data view
     * @return True if the path exists
     */
    default boolean contains(DataQuery path) {
        checkNotNull(path, "path");
        List<String> queryParts = path.getParts();

        int sz = queryParts.size();
        if (sz == 0) {
            return true;
        }

        K key = this.key(queryParts.get(0));
        if (sz == 1) {
            return this.contains(key);
        }

        Optional<Object> optional = this.get(key);
        return optional.isPresent()
                && optional.get() instanceof DataQueryable
                && ((DataQueryable) optional.get()).contains(path.popFirst());
    }

    /**
     * Returns whether this {@link DataView} contains an entry for all
     * provided {@link DataQuery} objects.
     *
     * @param path The path relative to this data view
     * @param paths The additional paths to check
     * @return True if all paths exist
     */
    default boolean contains(DataQuery path, DataQuery... paths) {
        checkNotNull(path, "DataQuery cannot be null!");
        checkNotNull(paths, "DataQuery varargs cannot be null!");

        if (!this.contains(path)) {
            return false;
        }
        for (DataQuery query : paths) {
            if (!this.contains(query)) {
                return false;
            }
        }
        return true; // we contain all paths :)
    }

    /**
     * Returns whether this {@link DataView} contains the given {@link Key}'s
     * defaulted {@link DataQuery}.
     *
     * @param key The key to get the data path relative to this data view
     * @return True if the path exists
     */
    default boolean contains(Key<?> key) {
        return this.contains(checkNotNull(key, "Key cannot be null!").getQuery());
    }

    /**
     * Returns whether this {@link DataView} contains the given {@link Key}es
     * defaulted {@link DataQuery}.
     *
     * @param key The key to get the data path relative to this data view
     * @param keys The additional keys to check
     * @return True if the path exists
     */
    default boolean contains(Key<?> key, Key<?>... keys) {
        checkNotNull(key, "Key cannot be null!");
        checkNotNull(keys, "Keys cannot be null!");

        if (!this.contains(key)) {
            return false;
        }
        for (Key<?> akey : keys) {
            if (!this.contains(checkNotNull(akey, "Cannot have a null key!").getQuery())) {
                return false;
            }
        }
        return true; // we contain all keys :)
    }

    /**
     * <p>Sets the given Object value according to the given path relative to
     * this {@link DataView}'s path.</p>
     *
     * <p>The value must be one of<br/>
     * * Allowed Types<br/>
     * * {@link DataSerializable}<br/>
     * * {@link CatalogType}<br/>
     * * have a {@link DataTranslator} registered in Sponge's {@link DataManager}<br/>
     * * {@link Map} (keys will be turned into queries vea toString())</p>
     *
     * @param path The path of the object to set
     * @param value The value of the data
     * @return This view, for chaining
     */
    DataView set(DataQuery path, Object value);

    /**
     * <p>Sets the given {@link Key}ed value according to the provided
     * {@link Key}'s {@link Key#getQuery()}.</p>
     *
     * <p>The value must be one of<br/>
     * * Allowed Types<br/>
     * * {@link DataSerializable}<br/>
     * * {@link CatalogType}<br/>
     * * have a {@link DataTranslator} registered in Sponge's {@link DataManager}<br/>
     * * {@link Map} (keys will be turned into queries vea toString())</p>
     *
     * @param key The key of the value to set
     * @param value The value of the data
     * @param <E> The type of value
     * @return This view, for chaining
     */
    <E> DataView set(Key<? extends BaseValue<E>> key, E value);

    /**
     * Removes the data associated to the given path relative to this
     * {@link DataView}'s path.
     * <p>Path can not be emtpy, to remove this {@link DataView}, call
     * the associated parent to remove this views name.</p>
     *
     * @param path The path of data to remove
     * @return This view, for chaining
     */
    DataView remove(DataQuery path);

    /**
     * Creates a new {@link DataMap} at the desired path.
     * <p>If any data existed at the given path, that data will be
     * overwritten with the newly constructed {@link DataView}.</p>
     *
     * @param path The path of the new data map
     * @return The newly created data map
     */
    DataMap createMap(DataQuery path);

    /**
     * Creates a new {@link DataList} at the desired path.
     * <p>If any data existed at the given path, that data will be
     * overwritten with the newly constructed {@link DataList}.</p>
     *
     * @param path The path of the new data list
     * @return The newly created data list
     */
    DataList createList(DataQuery path);

    /**
     * Gets the {@link DataMap} by path, if available.
     *
     * <p>If a {@link DataMap} does not exist, or the data residing at
     * the path is not an instance of a {@link DataMap}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The data map, if available
     */
    Optional<DataMap> getMap(DataQuery path);

    /**
     * Gets the {@link DataList} by path, if available.
     *
     * <p>If a {@link DataList} does not exist, or the data residing at
     * the path is not an instance of a {@link DataList}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The data list, if available
     */
    Optional<DataList> getList(DataQuery path);

    /**
     * Gets the {@link Boolean} by path, if available.
     *
     * <p>If a {@link Boolean} does not exist, or the data residing at
     * the path is not an instance of a {@link Boolean}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The boolean, if available
     */
    Optional<Boolean> getBoolean(DataQuery path);

    /**
     * Gets the {@link Byte} by path, if available.
     *
     * <p>If a {@link Byte} does not exist, or the data residing at
     * the path is not an instance of a {@link Byte}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The boolean, if available
     */
    Optional<Byte> getByte(DataQuery path);

    /**
     * Gets the {@link Character} by path, if available.
     *
     * <p>If a {@link Character} does not exist, or the data residing at
     * the path is not an instance of a {@link Character}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The boolean, if available
     */
    Optional<Short> getCharacter(DataQuery path);

    /**
     * Gets the {@link Short} by path, if available.
     *
     * <p>If a {@link Short} does not exist, or the data residing at
     * the path is not an instance of a {@link Short}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The boolean, if available
     */
    Optional<Short> getShort(DataQuery path);

    /**
     * Gets the {@link Integer} by path, if available.
     *
     * <p>If a {@link Integer} does not exist, or the data residing at
     * the path is not an instance of a {@link Integer}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The integer, if available
     */
    Optional<Integer> getInt(DataQuery path);

    /**
     * Gets the {@link Long} by path, if available.
     *
     * <p>If a {@link Long} does not exist, or the data residing at
     * the path is not an instance of a {@link Long}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The long, if available
     */
    Optional<Long> getLong(DataQuery path);

    /**
     * Gets the {@link Float} by path, if available.
     *
     * <p>If a {@link Float} does not exist, or the data residing at
     * the path is not an instance of a {@link Float}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The boolean, if available
     */
    Optional<Float> getFloat(DataQuery path);

    /**
     * Gets the {@link Double} by path, if available.
     *
     * <p>If a {@link Double} does not exist, or the data residing at
     * the path is not an instance of a {@link Double}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The double, if available
     */
    Optional<Double> getDouble(DataQuery path);

    /**
     * Gets the {@link String} by path, if available.
     *
     * <p>If a {@link String} does not exist, or the data residing at
     * the path is not an instance of a {@link String}, an absent is
     * returned.</p>
     *
     * @param path The path of the value to get
     * @return The string, if available
     */
    Optional<String> getString(DataQuery path);

    /**
     * Gets the boolean array at path, if available.
     *
     * <p>If the boolean array does not exist, or the data
     * residing at the path can not be coerced into a boolean array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The boolean array, if available
     */
    Optional<boolean[]> getBooleanArray(DataQuery path);

    /**
     * Gets the byte array at path, if available.
     *
     * <p>If the byte array does not exist, or the data
     * residing at the v can not be coerced into a byte array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The byte array, if available
     */
    Optional<byte[]> getByteArray(DataQuery path);

    /**
     * Gets the char array at path, if available.
     *
     * <p>If the char array does not exist, or the data
     * residing at the path can not be coerced into a char array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The char array, if available
     */
    Optional<char[]> getCharArray(DataQuery path);

    /**
     * Gets the short array at path, if available.
     *
     * <p>If the short array does not exist, or the data
     * residing at the path can not be coerced into a short array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The short array, if available
     */
    Optional<short[]> getShortArray(DataQuery path);

    /**
     * Gets the int array at path, if available.
     *
     * <p>If the int array does not exist, or the data
     * residing at the path can not be coerced into a int array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The int array, if available
     */
    Optional<int[]> getIntegerArray(DataQuery path);

    /**
     * Gets the long array at path, if available.
     *
     * <p>If the long array does not exist, or the data
     * residing at the path can not be coerced into a long array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The long array, if available
     */
    Optional<long[]> getLongArray(DataQuery path);

    /**
     * Gets the float array at path, if available.
     *
     * <p>If the float array does not exist, or the data
     * residing at the path can not be coerced into a float array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The float array, if available
     */
    Optional<float[]> getFloatArray(DataQuery path);

    /**
     * Gets the double array at path, if available.
     *
     * <p>If the double array does not exist, or the data
     * residing at the path can not be coerced into a double array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The double array, if available
     */
    Optional<double[]> getDoubleArray(DataQuery path);

    /**
     * Gets the {@link String} array at path, if available.
     *
     * <p>If the {@link String} array does not exist, or the data
     * residing at the path can not be coerced into a {@link String} array,
     * an absent is returned.</p>
     *
     * @param path The path of the value to get
     * @return The {@link String} array, if available
     */
    Optional<String[]> getStringArray(DataQuery path);

    /**
     * Gets the {@link DataSerializable} object by path, if available.
     *
     * <p>If a {@link DataSerializable} exists, but is not the proper class
     * type, or there is no data at the path given, an absent is returned.</p>
     *
     * <p>It is important that the {@link DataManager} provided is
     * the same one that has registered many of the
     * {@link DataBuilder}s to ensure the {@link DataSerializable}
     * requested can be returned.</p>
     *
     * @param <T> The type of {@link DataSerializable} object
     * @param path The path of the value to get
     * @param clazz The class of the {@link DataSerializable}
     * @return The deserialized object, if available
     */
    <T extends DataSerializable> Optional<T> getSerializable(DataQuery path, Class<T> clazz);

    <T> Optional<T> getObject(DataQuery path, Class<T> objectClass);

    /**
     * Gets the {@link CatalogType} object by path, if available.
     *
     * <p>If a {@link CatalogType} exists, but is not named properly, not
     * existing in a registry, or simply an invalid value will return
     * an empty value.</p>
     *
     * @param path The path of the value to get
     * @param catalogType The class of the dummy type
     * @param <T> The type of dummy
     * @return The dummy type, if available
     */
    <T extends CatalogType> Optional<T> getCatalogType(DataQuery path, Class<T> catalogType);

}
