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

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.util.Coerce;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents an object of data represented by a map.
 * <p>DataViews always exist within a {@link DataContainer} and can be used
 * for serialization.</p>
 *
 * Structure Allowed Types:<br/>
 * * {@link DataView}<br/>
 * * {@link List}&lt;Structure Type or Optimized List Type&gt;<br/>
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
 * * {@link String}<br/>
 *
 * Optimized List Allowed Types:<br/>
 * * boolean[]<br/>
 * * byte[]<br/>
 * * char[]<br/>
 * * short[]<br/>
 * * int[]<br/>
 * * long[]<br/>
 * * float[]<br/>
 * * double[]<br/>
 * * String[]<br/>
 */
public interface DataView<K> {

    /**
     * Gets the parent container of this DataView.
     *
     * <p>Every DataView will always have a {@link DataContainer}.</p>
     *
     * <p>For any {@link DataContainer}, this will return itself.</p>
     *
     * @return The parent container
     */
    DataContainer getContainer();

    /**
     * Gets the current path of this {@link DataView} from its root
     * {@link DataContainer}.
     *
     * <p>For any {@link DataContainer} itself, this will return an
     * empty string as it is the root of the path.</p>
     *
     * <p>The full path will always include this {@link DataView}s name
     * at the end of the path.</p>
     *
     * @return The path of this view originating from the root
     */
    DataQuery getCurrentPath();

    /**
     * Gets the name of this individual {@link DataView} in the path.
     *
     * <p>This will always be the final substring of the full path
     * from {@link #getCurrentPath()}.</p>
     *
     * @return The name of this DataView
     */
    String getName();

    /**
     * Gets the parent {@link DataView} of this view. The parent directly
     * contains this view according to the {@link #getCurrentPath()}.
     *
     * <p>For any {@link DataContainer}, this will return an absent parent.</p>
     *
     * @return The parent data view containing this view
     */
    Optional<DataView> getParent();

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
     * * {@link Map} (keys will be turned into queries vea toString())</p>
     *
     * @param key The key of the object to set
     * @param value The value of the data
     * @return This view, for chaining
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
     * {@link DataList} or Optimized List Allowed Type,
     * an absent is returned.</p>
     *
     * <p>Implementation note: If the underlying data is an Optimized List Allowed Type,
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
        return this.get(key).flatMap(Coerce::asBoolean);
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
        return this.get(key).flatMap(Coerce::asByte);
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
        return this.get(key).flatMap(Coerce::asChar);
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
        return this.get(key).flatMap(Coerce::asShort);
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
        return this.get(key).flatMap(Coerce::asInteger);
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
        return this.get(key).flatMap(Coerce::asLong);
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
        return this.get(key).flatMap(Coerce::asFloat);
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
        return this.get(key).flatMap(Coerce::asDouble);
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
        return this.get(key).flatMap(Coerce::asString);
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
    defualt Optional<boolean[]> getBooleanArray(K key) {
        return this.get(key).flatMap(Coerce::asBooleanArray);
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
    defualt Optional<byte[]> getByteArray(K key) {
        return this.get(key).flatMap(Coerce::asByteArray);
    }

    /**
     * Gets the char array at key, if available.
     *
     * <p>If the char array does not exist, or the data
     * residing at the key can not be coerced into a char array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The char array, if available
     */
    defualt Optional<char[]> getCharArray(K key) {
        return this.get(key).flatMap(Coerce::asCharArray);
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
    defualt Optional<short[]> getShortArray(K key) {
        return this.get(key).flatMap(Coerce::asShortArray);
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
    defualt Optional<int[]> getIntegerArray(K key) {
        return this.get(key).flatMap(Coerce::asIntArray);
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
    defualt Optional<long[]> getLongArray(K key) {
        return this.get(key).flatMap(Coerce::asLongArray);
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
    defualt Optional<float[]> getFloatArray(K key) {
        return this.get(key).flatMap(Coerce::asFloatArray);
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
    defualt Optional<double[]> getDoubleArray(K key) {
        return this.get(key).flatMap(Coerce::asDoubleArray);
    }

    /**
     * Gets the {@link String} array at key, if available.
     *
     * <p>If the {@link String} array does not exist, or the data
     * residing at the key can not be coerced into a {@link String} array,
     * an absent is returned.</p>
     *
     * @param key The key of the value to get
     * @return The {@link String} array, if available
     */
    defualt Optional<String[]> getStringArray(K key) {
        return this.get(key).flatMap(Coerce::asStringArray);
    }

    /**
     * Copies this {@link DataView} and all of it's contents into a new
     * {@link DataContainer}.
     *
     * <p>Note that the copy will not have the same path as this
     * {@link DataView} since it will be constructed with the top level path
     * being itself.</p>
     *
     * @return The newly constructed data view
     */
    DataContainer copy();

    /**
     * Copies this {@link DataView} and all of it's contents into a new
     * {@link DataContainer} with the given safety mode.
     *
     * <p>Note that the copy will not have the same path as this
     * {@link DataView} since it will be constructed with the top level path
     * being itself.</p>
     *
     * @param safety The safety mode of the copy
     * @return The newly constructed data view
     */
    DataContainer copy(SafetyMode safety);

    /**
     * Gets if this view contains no data.
     *
     * @return True if no data
     */
    boolean isEmpty();

    /**
     * Gets the {@link org.spongepowered.api.data.DataView.SafetyMode} of this data view.
     *
     * @return The safety mode
     */
    SafetyMode getSafetyMode();

    /**
     * The safety mode of the container.
     */
    enum SafetyMode {
        /**
         * All data added to the container will be cloned for safety.
         */
        ALL_DATA_CLONED,
        /**
         * All data added to the container will be cloned for safety.
         */
        CLONED_ON_SET,
        /**
         * No data added to the container will be cloned, useful for situations
         * with a large amount of data where the cloning would be too costly.
         */
        NO_DATA_CLONED

    }

}
