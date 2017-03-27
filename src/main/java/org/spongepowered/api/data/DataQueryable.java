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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.util.Coerce;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DataQueryable<K> implements DataView<K> {

    /**
     * Parses a {@link String} into a key type.
     *
     * @param key A {@link String} representation of a key
     * @return A key
     */
    protected abstract K key(String key);

    private Optional<DataQueryable> getQueryable(K key) {
        return this.get(key).filter(o -> o instanceof DataQueryable).map(o -> (DataQueryable) o);
    }

    private Optional<Object> get(DataQuery path) {
        checkNotNull(path, "path");
        List<String> parts = path.getParts();

        if (parts.isEmpty()) {
            return Optional.of(this);
        }

        K key = this.key(parts.get(0));
        if (parts.size() == 1) {
            return this.get(key);
        }

        return this.getQueryable(key).map(q -> q.get(path.popFirst()));
    }

    /**
     * Returns whether this {@link DataQueryable} contains the given path.
     *
     * @param path The path relative to this data view
     * @return True if the path exists
     */
    public boolean contains(DataQuery path) {
        checkNotNull(path, "path");
        List<String> parts = path.getParts();

        if (parts.isEmpty()) {
            return true;
        }

        K key = this.key(parts.get(0));
        if (parts.size() == 1) {
            return this.contains(key);
        }

        Optional<DataQueryable> optional = this.getQueryable(key);
        return optional.isPresent() && optional.get().contains(path.popFirst());
    }

    /**
     * Returns whether this {@link DataQueryable} contains an entry for all
     * provided {@link DataQuery} objects.
     *
     * @param path The path relative to this data view
     * @param paths The additional paths to check
     * @return True if all paths exist
     */
    public boolean contains(DataQuery path, DataQuery... paths) {
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
     * Returns whether this {@link DataQueryable} contains the given {@link Key}'s
     * defaulted {@link DataQuery}.
     *
     * @param key The key to get the data path relative to this data view
     * @return True if the path exists
     */
    public boolean contains(Key<?> key) {
        return this.contains(checkNotNull(key, "Key cannot be null!").getQuery());
    }

    /**
     * Returns whether this {@link DataQueryable} contains the given {@link Key}es
     * defaulted {@link DataQuery}.
     *
     * @param key The key to get the data path relative to this data view
     * @param keys The additional keys to check
     * @return True if the path exists
     */
    public boolean contains(Key<?> key, Key<?>... keys) {
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
     * this {@link DataQueryable}'s path.</p>
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
    public DataQueryable<K> set(DataQuery path, Object value) {
        checkNotNull(path, "path");
        checkNotNull(value, "value");
        List<String> parts = path.getParts();
        checkArgument(parts.isEmpty(), "The query not be empty");

        K key = this.key(parts.get(0));
        if (parts.size() == 1) {
            this.set(key, value);
        } else {
            // Get or create a DataQueryable at key, and recursively call set() on that DataQueryable
            this.getQueryable(key)
                    .orElseGet(() -> this.createMap(key))
                    .set(path.popFirst(), value);
        }

        return this;
    }

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
    public <E> DataQueryable<K> set(Key<? extends BaseValue<E>> key, E value) {
        return this.set(checkNotNull(key, "Key was null!").getQuery(), value);
    }

    /**
     * Removes the data associated to the given path relative to this
     * {@link DataView}'s path.
     * <p>Path can not be emtpy, to remove this {@link DataView}, call
     * the associated parent to remove this views name.</p>
     *
     * @param path The path of data to remove
     * @return This view, for chaining
     */
    public DataQueryable<K> remove(DataQuery path) {
        checkNotNull(path, "path");
        List<String> parts = path.getParts();
        checkArgument(parts.isEmpty(), "The query can not be empty");

        K key = this.key(parts.get(0));
        if (parts.size() == 1) {
            this.remove(key);
        } else {
            this.getQueryable(key).ifPresent(dataQueryable -> dataQueryable.remove(path.pop()));
        }
        return this;
    }

    /**
     * Creates a new {@link DataMap} at the desired path.
     * <p>If any data existed at the given path, that data will be
     * overwritten with the newly constructed {@link DataView}.</p>
     *
     * @param path The path of the new data map
     * @return The newly created data map
     */
    public DataMap createMap(DataQuery path) {
        checkNotNull(path, "path");
        List<String> parts = path.getParts();
        checkArgument(parts.isEmpty(), "The query not be empty");

        K key = this.key(parts.get(0));
        if (parts.size() == 1) {
            return this.createMap(key);
        }

        return this.getQueryable(key)
                .orElseGet(() -> this.createMap(key))
                .createMap(path.popFirst());
    }

    /**
     * Creates a new {@link DataList} at the desired path.
     * <p>If any data existed at the given path, that data will be
     * overwritten with the newly constructed {@link DataList}.</p>
     *
     * @param path The path of the new data list
     * @return The newly created data list
     */
    public DataList createList(DataQuery path) {
        checkNotNull(path, "path");
        List<String> parts = path.getParts();
        checkArgument(parts.isEmpty(), "The query not be empty");

        K key = this.key(parts.get(0));
        if (parts.size() == 1) {
            return this.createList(key);
        }

        return this.getQueryable(key)
                .orElseGet(() -> this.createMap(key))
                .createList(path.popFirst());
    }

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
    public Optional<DataMap> getMap(DataQuery path) {
        return this.get(path).filter(o -> o instanceof DataMap).map(o -> (DataMap) o);
    }

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
    public Optional<DataList> getList(DataQuery path) {
        return this.get(path).filter(o -> o instanceof DataList).map(o -> (DataList) o);
    }

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
    public Optional<Boolean> getBoolean(DataQuery path) {
        return this.get(path).flatMap(Coerce::asBoolean);
    }

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
    public Optional<Byte> getByte(DataQuery path) {
        return this.get(path).flatMap(Coerce::asByte);
    }

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
    public Optional<Character> getCharacter(DataQuery path) {
        return this.get(path).flatMap(Coerce::asChar);
    }

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
    public Optional<Short> getShort(DataQuery path) {
        return get(path).flatMap(Coerce::asShort);
    }

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
    public Optional<Integer> getInt(DataQuery path) {
        return get(path).flatMap(Coerce::asInteger);
    }

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
    public Optional<Long> getLong(DataQuery path) {
        return get(path).flatMap(Coerce::asLong);
    }

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
    public Optional<Float> getFloat(DataQuery path) {
        return get(path).flatMap(Coerce::asFloat);
    }

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
    public Optional<Double> getDouble(DataQuery path) {
        return get(path).flatMap(Coerce::asDouble);
    }

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
    public Optional<String> getString(DataQuery path) {
        return get(path).flatMap(Coerce::asString);
    }

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
    public Optional<boolean[]> getBooleanArray(DataQuery path) {
        return get(path).flatMap(Coerce::asBooleanArray);
    }

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
    public Optional<byte[]> getByteArray(DataQuery path) {
        return get(path).flatMap(Coerce::asByteArray);
    }

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
    public Optional<char[]> getCharArray(DataQuery path) {
        return get(path).flatMap(Coerce::asCharArray);
    }

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
    public Optional<short[]> getShortArray(DataQuery path) {
        return get(path).flatMap(Coerce::asShortArray);
    }

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
    public Optional<int[]> getIntArray(DataQuery path) {
        return get(path).flatMap(Coerce::asIntArray);
    }

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
    public Optional<long[]> getLongArray(DataQuery path) {
        return get(path).flatMap(Coerce::asLongArray);
    }

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
    public Optional<float[]> getFloatArray(DataQuery path) {
        return get(path).flatMap(Coerce::asFloatArray);
    }

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
    public Optional<double[]> getDoubleArray(DataQuery path) {
        return get(path).flatMap(Coerce::asDoubleArray);
    }

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
    public Optional<String[]> getStringArray(DataQuery path) {
        return get(path).flatMap(Coerce::asStringArray);
    }

    /**
     * Gets a {@link DataSerializable}, {@link CatalogType}, or {@link DataTranslator}-able
     * object registered in Sponge by key, if available.
     *
     * <p>If the data at the key is a {@link DataMap}
     * and {@code type} is a {@link DataSerializable},
     * and the {@link DataSerializable} has a corresponding {@link DataBuilder} registered
     * in Sponge's DataManager, present is returned.</p>
     *
     * <p>If the data at the key is a {@link DataMap}
     * and a {@link DataTranslator} corresponding to {@code type} is registered
     * in Sponge's DataManager, present is returned.</p>
     *
     * <p>If {@code type} is a {@link CatalogType} registered in Sponge
     * and the data at the key can be coerced into a {@link String}
     * representing the specific {@link CatalogType}, present is returned.</p>
     *
     * @param <T> The type of object
     * @param key The key of the value to get
     * @param type The class of the object
     * @return The deserialized object, if available
     */
    public <T extends DataSerializable> Optional<T> getSpongeObject(K key, Class<T> type) {
        checkNotNull(type, "type");
        return this.get(key).flatMap(o -> this.coerseSpongeObject(o, type));
    }

    /**
     * Gets a {@link DataSerializable}, {@link CatalogType}, or {@link DataTranslator}-able
     * object registered in Sponge at path, if available.
     *
     * <p>If the data at the path is a {@link DataMap}
     * and {@code type} is a {@link DataSerializable},
     * and the {@link DataSerializable} has a corresponding {@link DataBuilder} registered
     * in Sponge's DataManager, present is returned.</p>
     *
     * <p>If the data at the path is a {@link DataMap}
     * and a {@link DataTranslator} corresponding to {@code type} is registered
     * in Sponge's DataManager, present is returned.</p>
     *
     * <p>If {@code type} is a {@link CatalogType} registered in Sponge
     * and the data at the path can be coerced into a {@link String}
     * representing the specific {@link CatalogType}, present is returned.</p>
     *
     * @param <T> The type of object
     * @param path The key of the value to get
     * @param type The class of the object
     * @return The deserialized object, if available
     */
    public <T extends DataSerializable> Optional<T> getSpongeObject(DataQuery path, Class<T> type) {
        checkNotNull(type, "type");
        return this.get(path).flatMap(o -> this.coerseSpongeObject(o, type));
    }

    //TODO: move to Coerce?
    @SuppressWarnings("unchecked")
    private <T extends DataSerializable> Optional<T> coerseSpongeObject(@Nonnull Object obj, @Nonnull Class<T> type) {
        if (obj instanceof DataMap) {

            // See if type is a DataSerializable, in which case it *might* have a builder
            if (DataSerializable.class.isAssignableFrom(type)) {
                Optional<DataBuilder<T>> builder = Sponge.getDataManager().getBuilder(type);
                if (builder.isPresent()) {
                    return builder.get().build((DataMap) obj);
                } // else: ok, it did'nt have a builder, move on
            }

            // Try using a data translator
            return Sponge.getDataManager().getTranslator(type)
                    .map(translator -> translator.translate((DataMap) obj));
        }
        if (CatalogType.class.isAssignableFrom(type)) {
            // The compiler does not like the `(Class) type` hack. Added @SuppressWarnings("unchecked")
            return Coerce.asString(obj).flatMap(s -> Sponge.getRegistry().getType((Class) type, s));
        }
        return Optional.empty();
    }
}
