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
import org.spongepowered.api.util.Coerce2;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * A key-value like data structure allowing only a fixed number of types (see Allowed Types)
 *
 * <p>{@link DataView}s make a good IR (Intermediate Representation) for data when serializing,
 * as you only need one object specific serializer to go to/form a DataView,
 * than a generic DataView to any binary/config format serializer can be used!</p>
 *
 * <p>I recommend that serializer methods (object -> DataView) do not create any root
 * {@link DataView}s of there own. Instead they should be provided with a {@link DataView} in
 * which to store there fields in.<br/>
 * Example serializable object:<br/>
 * {@code
 * public class Foo {
 *
 *     private int someInt;
 *     private String someString;
 *     private Bar bar; // assume bar also has a serialize() method
 *
 *     public void serialize(DataMap storage) {
 *         storage.set("someInt", someInt) // A DataQuery may be used instead of "someInt"
 *                .set("someString", someString);
 *         bar.serialize(storage.createMap("bar"));
 *     }
 * }
 * }</p>
 *
 * TODO: Use the recommendation in all of Sponge... I know... lots of breaking changes
 *
 * Structure Allowed Types:<br/>
 * * {@link DataMap}<br/>
 * * {@link DataList}<br/>
 *
 * Primitive Allowed Types:<br/>
 * * {@link Boolean}<br/>
 * * {@link Byte}<br/>
 * * {@link Character}<br/>
 * * {@link Short}<br/>
 * * {@link Integer}<br/>
 * * {@link Long}<br/>
 * * {@link Float}<br/>
 * * {@link Double}<br/>
 *
 * Array Allowed Types:<br/>
 * * boolean[]<br/>
 * * byte[]<br/>
 * * {@link String}<br/>
 * * short[]<br/>
 * * int[]<br/>
 * * long[]<br/>
 * * float[]<br/>
 * * double[]<br/>
 *
 * <p>Note: Sub-interfaces (DataList and DataMap) may further restrict the Allowed Types.</p>
 *
 * @param <K> The key type
 */
public interface DataView<K> {

    /**
     * Gets the parent {@link DataView} of this view.
     *
     * <p>If this is the root {@link DataView}, than an absent is returned.</p>
     *
     * @return The parent data view containing this view
     */
    Optional<DataView> getParent();

    /**
     * Gets the number of elements in this {@link DataView}.
     *
     * @return How many elements there are
     */
    int size();

    /**
     * Returns whether this {@link DataView} contains the given key.
     *
     * @param key The key
     * @return True if the key exists
     */
    boolean contains(K key);

    /**
     * Gets an object from the desired key. If the key is not defined,
     * an absent Optional is returned.
     *
     * <p>The returned Object shall be one of the Allowed Types.</p>
     *
     * <p>Warning: Inconsistency leak!
     * Only use this method if you don't care what specific type the returned Object is.
     * Example: If you set an Integer, get() may not return an Integer,
     * but another Allowed Type that can losslessly be coerced into an Integer!</p>
     *
     * @param key The key to the Object
     * @return The Object, if available
     */
    Optional<Object> get(K key);

    /**
     * <p>Sets the value at key to the given Object</p>
     *
     * <p>The value must be one of<br/>
     * * Allowed Types<br/>
     * * {@link DataSerializable}<br/>
     * * {@link CatalogType}<br/>
     * * have a {@link DataTranslator} registered in Sponge's {@link DataManager}<br/>
     * * {@link Map} (will be coerced into a {@link DataMap}, or error on failure)<br/>
     * * {@link Collection} (will be coerced into a {@link DataList}/array, or error on failure)</p>
     *
     * @param key The key of the object to set
     * @param value The value of the data
     * @return This view, for chaining
     * @throws IllegalArgumentException thrown when {@code element} is of an unsupported type
     */
    DataView set(K key, Object value);

    /**
     * Removes the data associated with the given key.
     *
     * @param key The key of the data to remove
     * @return This view, for chaining
     */
    DataView remove(K key);

    /**
     * Creates a new {@link DataMap} at the desired key.
     * <p>If any data existed at the given key, that data will be
     * overwritten with the newly constructed {@link DataMap}.</p>
     *
     * @param key The key of the new {@link DataMap}
     * @return The newly created {@link DataMap}
     */
    DataMap createMap(K key);

    /**
     * Creates a new {@link DataList} at the desired key.
     * <p>If any data existed at the given key, that data will be
     * overwritten with the newly constructed {@link DataList}.</p>
     *
     * @param key The key of the new {@link DataList}
     * @return The newly created {@link DataList}
     */
    DataList createList(K key);

    /**
     * Gets the {@link DataMap} by path, if available.
     *
     * <p>If a {@link DataMap} does not exist, or the data residing at
     * the path is not an instance of a {@link DataMap}, an absent is
     * returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link DataMap}, if available
     */
    default Optional<DataMap> getMap(K key) {
        return this.get(key).filter(o -> o instanceof DataMap).map(o -> (DataMap) o);
    }

    /**
     * Gets the {@link DataList} by key, if available.
     *
     * <p>If the data residing at the key is not a
     * {@link DataList} or Array Allowed Types,
     * an absent is returned.</p>
     *
     * <p>Implementation note: If the underlying data is an Array Allowed Types,
     * it must also be converted into the {@link DataList} in order for mutations to work.<p/>
     *
     * @param key The key to the value to get
     * @return The {@link DataList}, if available
     */
    default Optional<DataList> getList(K key) {
        return this.get(key).filter(o -> o instanceof DataList).map(o -> (DataList) o);
    }

    /**
     * Gets the {@link Boolean} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Boolean}
     * and can not be coerced into a {@link Boolean}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Boolean}, if available
     */
    default Optional<Boolean> getBoolean(K key) {
        return this.get(key).flatMap(Coerce2::asBoolean);
    }

    /**
     * Gets the {@link Byte} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Byte}
     * and can not be coerced into a {@link Byte}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Byte}, if available
     */
    default Optional<Byte> getByte(K key) {
        return this.get(key).flatMap(Coerce2::asByte);
    }

    /**
     * Gets the {@link Character} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Character}
     * and can not be coerced into a {@link Character}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Character}, if available
     */
    default Optional<Character> getCharacter(K key) {
        return this.get(key).flatMap(Coerce2::asChar);
    }

    /**
     * Gets the {@link Short} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Short}
     * and can not be coerced into a {@link Short}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Short}, if available
     */
    default Optional<Short> getShort(K key) {
        return this.get(key).flatMap(Coerce2::asShort);
    }

    /**
     * Gets the {@link Integer} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Integer}
     * and can not be coerced into a {@link Integer}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Integer}, if available
     */
    default Optional<Integer> getInt(K key) {
        return this.get(key).flatMap(Coerce2::asInteger);
    }

    /**
     * Gets the {@link Long} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Long}
     * and can not be coerced into a {@link Long}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Long}, if available
     */
    default Optional<Long> getLong(K key) {
        return this.get(key).flatMap(Coerce2::asLong);
    }

    /**
     * Gets the {@link Float} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Float}
     * and can not be coerced into a {@link Float}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Float}, if available
     */
    default Optional<Float> getFloat(K key) {
        return this.get(key).flatMap(Coerce2::asFloat);
    }

    /**
     * Gets the {@link Double} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link Double}
     * and can not be coerced into a {@link Double}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link Double}, if available
     */
    default Optional<Double> getDouble(K key) {
        return this.get(key).flatMap(Coerce2::asDouble);
    }

    /**
     * Gets the boolean array at key, if available.
     *
     * <p>If the boolean array does not exist, or the data
     * residing at the key can not be coerced into a boolean array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The boolean array, if available
     */
    default Optional<boolean[]> getBooleanArray(K key) {
        return this.get(key).flatMap(Coerce2::asBooleanArray);
    }

    /**
     * Gets the byte array at key, if available.
     *
     * <p>If the byte array does not exist, or the data
     * residing at the key can not be coerced into a byte array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The byte array, if available
     */
    default Optional<byte[]> getByteArray(K key) {
        return this.get(key).flatMap(Coerce2::asByteArray);
    }

    /**
     * Gets the {@link String} by key, if available.
     *
     * <p>If the data residing at the key is not a {@link String}
     * and can not be coerced into a {@link String}, an absent is returned.</p>
     *
     * @param key The key to the value to get
     * @return The {@link String}, if available
     */
    default Optional<String> getString(K key) {
        return this.get(key).flatMap(Coerce2::asString);
    }

    /**
     * Gets the short array at key, if available.
     *
     * <p>If the short array does not exist, or the data
     * residing at the key can not be coerced into a short array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The short array, if available
     */
    default Optional<short[]> getShortArray(K key) {
        return this.get(key).flatMap(Coerce2::asShortArray);
    }

    /**
     * Gets the int array at key, if available.
     *
     * <p>If the int array does not exist, or the data
     * residing at the key can not be coerced into a int array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The int array, if available
     */
    default Optional<int[]> getIntegerArray(K key) {
        return this.get(key).flatMap(Coerce2::asIntArray);
    }

    /**
     * Gets the long array at key, if available.
     *
     * <p>If the long array does not exist, or the data
     * residing at the key can not be coerced into a long array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The long array, if available
     */
    default Optional<long[]> getLongArray(K key) {
        return this.get(key).flatMap(Coerce2::asLongArray);
    }

    /**
     * Gets the float array at key, if available.
     *
     * <p>If the float array does not exist, or the data
     * residing at the key can not be coerced into a float array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The float array, if available
     */
    default Optional<float[]> getFloatArray(K key) {
        return this.get(key).flatMap(Coerce2::asFloatArray);
    }

    /**
     * Gets the double array at key, if available.
     *
     * <p>If the double array does not exist, or the data
     * residing at the key can not be coerced into a double array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The double array, if available
     */
    default Optional<double[]> getDoubleArray(K key) {
        return this.get(key).flatMap(Coerce2::asDoubleArray);
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
    default <T extends DataSerializable> Optional<T> getSpongeObject(K key, Class<T> type) {
        checkNotNull(type, "type");
        return this.get(key).flatMap(o -> Coerce2.asSpongeObject(o, type));
    }

    /**
     * Gets if this view contains no data.
     *
     * @return True if no data
     */
    boolean isEmpty();

    /*
     * ===========================
     * ==== queryable methods ====
     * ===========================
     */

    /**
     * Parses a {@link String} into a key type.
     *
     * <p>If the {@link String} can not be parsed than an invalid key is returned such
     * that calls to get methods will return empty, etc.<p/>
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
    boolean contains(DataQuery path);

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
    DataView<K> set(DataQuery path, Object value);

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
    default <E> DataView<K> set(Key<? extends BaseValue<E>> key, E value) {
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
    DataView<K> remove(DataQuery path);

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
    Optional<Character> getCharacter(DataQuery path);

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
    Optional<int[]> getIntArray(DataQuery path);

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
    <T extends DataSerializable> Optional<T> getSpongeObject(DataQuery path, Class<T> type);
}
