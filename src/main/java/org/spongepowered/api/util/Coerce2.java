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
package org.spongepowered.api.util;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataList;
import org.spongepowered.api.data.DataMap;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.lang.reflect.Array;
import java.util.Optional;

/**
 * Utility class for coercing unknown values to specific target types.
 */
@NonnullByDefault
public final class Coerce2 {

    /**
     * No subclasses for you.
     */
    private Coerce2() {}

    /**
     * Gets the given object as a {@link Boolean}.
     *
     * @param obj The object to translate
     * @return The boolean, if available
     */
    public static Optional<Boolean> asBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return Optional.of((Boolean) obj);
        }

        Optional<Integer> optional = asInteger(obj);
        if (optional.isPresent()) {
            return optional.map(i -> i != 0); // 0 = false, anything else = true (just like C)
        }

        String str = obj.toString().trim();
        if (str.equalsIgnoreCase("true")
                || str.equalsIgnoreCase("yes")
                || str.equalsIgnoreCase("t")
                || str.equalsIgnoreCase("y")) {
            return Optional.of(true);
        }
        if (str.equalsIgnoreCase("false")
                || str.equalsIgnoreCase("no")
                || str.equalsIgnoreCase("f")
                || str.equalsIgnoreCase("n")) {
            return Optional.of(false);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a {@link Byte}.
     *
     * <p>Note that this does not translate numbers spelled out as strings.</p>
     *
     * @param obj The object to translate
     * @return The byte value, if available
     */
    public static Optional<Byte> asByte(Object obj) {
        if (obj instanceof Number) {
            return Optional.of(((Number) obj).byteValue());
        }

        try {
            return Optional.of(Byte.valueOf(Coerce2.sanitiseNumber(obj)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the given object as a {@link Character}.
     *
     * @param obj The object to translate
     * @return The character, if available
     */
    public static Optional<Character> asChar(Object obj) {
        if (obj instanceof Character) {
            return Optional.of((Character) obj);
        }

        String str = obj.toString();
        if (str.length() > 0) {
            return Optional.of(str.charAt(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets the given object as a {@link Short}.
     *
     * <p>Note that this does not translate numbers spelled out as strings.</p>
     *
     * @param obj The object to translate
     * @return The short value, if available
     */
    public static Optional<Short> asShort(Object obj) {
        if (obj instanceof Number) {
            return Optional.of(((Number) obj).shortValue());
        }

        try {
            // use parseFloat() so dots don't cause it to fail
            return Optional.of((short) Float.parseFloat(Coerce2.sanitiseNumber(obj)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the given object as a {@link Integer}.
     *
     * <p>Note that this does not translate numbers spelled out as strings.</p>
     *
     * @param obj The object to translate
     * @return The integer value, if available
     */
    public static Optional<Integer> asInteger(Object obj) {
        if (obj instanceof Number) {
            return Optional.of(((Number) obj).intValue());
        }

        try {
            // use parseDouble() so dots don't cause it to fail
            return Optional.of((int) Double.parseDouble(Coerce2.sanitiseNumber(obj)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the given object as a {@link Long}.
     *
     * <p>Note that this does not translate numbers spelled out as strings.</p>
     *
     * @param obj The object to translate
     * @return The long value, if available
     */
    public static Optional<Long> asLong(Object obj) {
        if (obj instanceof Number) {
            return Optional.of(((Number) obj).longValue());
        }

        String str = Coerce2.sanitiseNumber(obj);

        try {
            return Optional.of(Long.valueOf(str));
        } catch (NumberFormatException e) {
            try {
                return Optional.of((long) Double.parseDouble(str));
            } catch (NumberFormatException e2) {
                return Optional.empty();
            }
        }
    }

    /**
     * Gets the given object as a {@link Float}.
     *
     * <p>Note that this does not translate numbers spelled out as strings.</p>
     *
     * @param obj The object to translate
     * @return The float value, if available
     */
    public static Optional<Float> asFloat(Object obj) {
        if (obj instanceof Number) {
            return Optional.of(((Number) obj).floatValue());
        }

        try {
            return Optional.of(Float.valueOf(Coerce2.sanitiseNumber(obj)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the given object as a {@link Double}.
     *
     * <p>Note that this does not translate numbers spelled out as strings.</p>
     *
     * @param obj The object to translate
     * @return The double value, if available
     */
    public static Optional<Double> asDouble(Object obj) {
        if (obj instanceof Number) {
            return Optional.of(((Number) obj).doubleValue());
        }

        try {
            return Optional.of(Double.valueOf(Coerce2.sanitiseNumber(obj)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the given object as a {@link String}.
     *
     * @param obj The object to translate
     * @return The boolean, if available
     */
    public static Optional<String> asString(Object obj) {
        if (obj instanceof char[]) {
            return Optional.of(String.valueOf((char[]) obj));
        } else {
            return Optional.of(obj.toString());
        }
    }

    /**
     * Gets the given object as a boolean[].
     *
     * @param obj The object to translate
     * @return The boolean[], if available
     */
    public static Optional<boolean[]> asBooleanArray(Object obj) {
        if (obj instanceof boolean[]) {
            return Optional.of((boolean[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        boolean[] booleans = new boolean[Array.getLength(obj)];

        Optional<StrArray> strsOpt = wrapStrArray(obj);
        if (strsOpt.isPresent()) {
            StrArray strings = strsOpt.get();

            int i;
            for (i = 0; i < booleans.length; i++) {
                String str = strings.stringValue(i).trim();
                if (str.equalsIgnoreCase("true")
                        || str.equalsIgnoreCase("yes")
                        || str.equalsIgnoreCase("t")
                        || str.equalsIgnoreCase("y")) {
                    booleans[i] = true;
                } else if (str.equalsIgnoreCase("false")
                        || str.equalsIgnoreCase("no")
                        || str.equalsIgnoreCase("f")
                        || str.equalsIgnoreCase("n")) {
                    continue;
                }
                break; // We hit a string that can not be turned into a boolean!
            }
            if (i == strings.size()) {
                return Optional.of(booleans);
            }
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();

            for (int i = 0; i < numbers.size(); i++) {
                booleans[i] = numbers.intValue(i) != 0; // 0 = false, anything else = true (just like C)
            }
            return Optional.of(booleans);
        }

        return Optional.empty();
    }

    /**
     * Gets the given object as a byte[].
     *
     * @param obj The object to translate
     * @return The byte[], if available
     */
    public static Optional<byte[]> asByteArray(Object obj) {
        if (obj instanceof byte[]) {
            return Optional.of((byte[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            byte[] bytes = new byte[numbers.size()];

            for (int i = 0; i < numbers.size(); i++) {
                bytes[i] = (byte) numbers.intValue(i);
            }
            return Optional.of(bytes);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a char[].
     *
     * @param obj The object to translate
     * @return The char[], if available
     */
    public static Optional<char[]> asCharArray(Object obj) {
        if (obj instanceof char[]) {
            return Optional.of((char[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        char[] chars = new char[Array.getLength(obj)];

        if (obj instanceof String[]) {
            String[] strings = (String[]) obj;

            int i;
            for (i = 0; i < strings.length; i++) {
                String str = strings[i];
                if (str.length() < 1) {
                    break;
                }
                chars[i] = str.charAt(0);
            }
            if (i == strings.length) {
                return Optional.of(chars);
            }
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();

            for (int i = 0; i < numbers.size(); i++) {
                // Integer.valueOf(int) never returns a 0 length String so we are safe
                chars[i] = Integer.toString(numbers.intValue(i)).charAt(0);
            }
            return Optional.of(chars);
        }

        return Optional.empty();
    }

    /**
     * Gets the given object as a short[].
     *
     * @param obj The object to translate
     * @return The short[], if available
     */
    public static Optional<short[]> asShortArray(Object obj) {
        if (obj instanceof short[]) {
            return Optional.of((short[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            short[] shorts = new short[numbers.size()];

            for (int i = 0; i < numbers.size(); i++) {
                shorts[i] = (short) numbers.intValue(i);
            }
            return Optional.of(shorts);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a int[].
     *
     * @param obj The object to translate
     * @return The int[], if available
     */
    public static Optional<int[]> asIntArray(Object obj) {
        if (obj instanceof int[]) {
            return Optional.of((int[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            int[] ints = new int[numbers.size()];

            for (int i = 0; i < numbers.size(); i++) {
                ints[i] = numbers.intValue(i);
            }
            return Optional.of(ints);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a long[].
     *
     * @param obj The object to translate
     * @return The long[], if available
     */
    public static Optional<long[]> asLongArray(Object obj) {
        if (obj instanceof long[]) {
            return Optional.of((long[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            long[] longs = new long[numbers.size()];

            for (int i = 0; i < numbers.size(); i++) {
                longs[i] = numbers.longValue(i);
            }
            return Optional.of(longs);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a float[].
     *
     * @param obj The object to translate
     * @return The float[], if available
     */
    public static Optional<float[]> asFloatArray(Object obj) {
        if (obj instanceof float[]) {
            return Optional.of((float[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            float[] floats = new float[numbers.size()];

            for (int i = 0; i < numbers.size(); i++) {
                floats[i] = numbers.floatValue(i);
            }
            return Optional.of(floats);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a double[].
     *
     * @param obj The object to translate
     * @return The double[], if available
     */
    public static Optional<double[]> asDoubleArray(Object obj) {
        if (obj instanceof double[]) {
            return Optional.of((double[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            double[] doubles = new double[numbers.size()];

            for (int i = 0; i < numbers.size(); i++) {
                doubles[i] = numbers.doubleValue(i);
            }
            return Optional.of(doubles);
        }
        return Optional.empty();
    }

    /**
     * Gets the given object as a {@link String}[].
     *
     * @param obj The object to translate
     * @return The {@link String}[], if available
     */
    public static Optional<String[]> asStringArray(Object obj) {
        if (obj instanceof String[]) {
            return Optional.of((String[]) obj); // fast path
        }
        if (!obj.getClass().isArray()) {
            return Optional.empty();
        }

        Optional<StrArray> strsOpt = wrapStrArray(obj);
        if (strsOpt.isPresent()) {
            StrArray strs = strsOpt.get();
            String[] strings = new String[strs.size()];

            for (int i = 0; i < strings.length; i++) {
                strings[i] = strs.stringValue(i);
            }
            return Optional.of(strings);
        }

        Optional<NumArray> numsOpt = wrapNumArray(obj);
        if (numsOpt.isPresent()) {
            NumArray numbers = numsOpt.get();
            String[] strings = new String[numbers.size()];

            if (numbers.isFloating()) {
                for (int i = 0; i < numbers.size(); i++) {
                    strings[i] = Double.toString(numbers.doubleValue(i));
                }
            } else {
                for (int i = 0; i < numbers.size(); i++) {
                    strings[i] = Long.toString(numbers.longValue(i));
                }
            }
            return Optional.of(strings);
        }

        return Optional.empty();
    }

    /**
     * Gets the given object as a {@link DataSerializable}, {@link CatalogType}, or {@link DataTranslator}-able
     * object registered in Sponge, if available.
     *
     * <p>If {@code obj} is a {@link DataMap}
     * and {@code type} is a {@link DataSerializable},
     * and the {@link DataSerializable} has a corresponding {@link DataBuilder} registered
     * in Sponge's DataManager, present is returned.</p>
     *
     * <p>If {@code obj} is a {@link DataMap}
     * and a {@link DataTranslator} corresponding to {@code type} is registered
     * in Sponge's DataManager, present is returned.</p>
     *
     * <p>If {@code type} is a {@link CatalogType} registered in Sponge
     * and {@code obj} can be coerced into a {@link String}
     * representing the specific {@link CatalogType}, present is returned.</p>
     *
     * @param <T> The type of object
     * @param type The class of the object
     * @return The deserialized object, if available
     */
    @SuppressWarnings("unchecked")
    public static <T extends DataSerializable> Optional<T> asSpongeObject(Object obj, Class<T> type) {
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

    private static String sanitiseNumber(Object obj) {
        return obj.toString().trim();
    }

    /**
     * Wraps a primitive number array in a {@link NumArray}
     */
    private static Optional<NumArray> wrapNumArray(Object obj) {
        if (obj instanceof byte[]) {
            return Optional.of(new ByteArray((byte[]) obj));
        } else if (obj instanceof short[]) {
            return Optional.of(new ShortArray((short[]) obj));
        } else if (obj instanceof int[]) {
            return Optional.of(new IntegerArray((int[]) obj));
        } else if (obj instanceof long[]) {
            return Optional.of(new LongArray((long[]) obj));
        } else if (obj instanceof float[]) {
            return Optional.of(new FloatArray((float[]) obj));
        } else if (obj instanceof double[]) {
            return Optional.of(new DoubleArray((double[]) obj));
        }

        Optional<StrArray> strsOpt = wrapStrArray(obj);
        if (strsOpt.isPresent()) {
            StrArray strings = strsOpt.get();

            try {
                long[] parsed = new long[strings.size()];
                int at;
                for (at = 0; at < parsed.length; at++) {
                    String str = strings.stringValue(at);
                    if (str.contains(".")) {
                        break; // We have doubles
                    }
                    parsed[at] = Long.parseLong(sanitiseNumber(str));
                }

                if (at == parsed.length) {
                    return Optional.of(new LongArray(parsed));
                } else {
                    double[] parsed2 = new double[strings.size()];
                    for (int copy = 0; copy < at; copy++) {
                        // copy the long's we have already parsed (its faster than re-parsing the strings)
                        parsed2[copy] = parsed[copy];
                    }

                    for (; at < parsed.length; at++) {
                        String str = strings.stringValue(at);
                        if (str.contains(".")) {
                            break; // We have doubles
                        }
                        parsed2[at] = Double.parseDouble(sanitiseNumber(str));
                    }
                    return Optional.of(new DoubleArray(parsed2));
                }
            } catch (NumberFormatException e) { /* Optional.empty() is returned */ }
        }
        return Optional.empty();
    }

    /**
     * Wraps a primitive array of string-like elements in a {@link StrArray}
     */
    private static Optional<StrArray> wrapStrArray(Object obj) {
        if (obj instanceof String[]) {
            return Optional.of(new StringArray((String[]) obj));
        } else if (obj instanceof char[]) {
            return Optional.of(new CharacterArray((char[]) obj));
        } else if (obj instanceof DataList) {
            // TODO: Make shore its a list of char[]'s!!!
            cause an error so you dont forget;

            return Optional.of(new CharArrayListArray((DataList) obj));
        }
        return Optional.empty();
    }

    /*
     * Helper classes to wrap number arrays so they can be dalt with generically
     * (saves a lot of special cases in the array coerce methods)
     */

    private interface NumArray {

        int size();

        boolean isFloating();

        int intValue(int index);
        long longValue(int index);
        float floatValue(int index);
        double doubleValue(int index);
    }

    private static class ByteArray implements NumArray {

        private byte[] array;

        ByteArray(byte[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public boolean isFloating() { return false; }
        @Override public int intValue(int index) { return array[index]; }
        @Override public long longValue(int index) { return array[index]; }
        @Override public float floatValue(int index) { return array[index]; }
        @Override public double doubleValue(int index) { return array[index]; }
    }

    private static class ShortArray implements NumArray {

        private short[] array;

        ShortArray(short[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public boolean isFloating() { return false; }
        @Override public int intValue(int index) { return array[index]; }
        @Override public long longValue(int index) { return array[index]; }
        @Override public float floatValue(int index) { return array[index]; }
        @Override public double doubleValue(int index) { return array[index]; }
    }

    private static class IntegerArray implements NumArray {

        private int[] array;

        IntegerArray(int[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public boolean isFloating() { return false; }
        @Override public int intValue(int index) { return array[index]; }
        @Override public long longValue(int index) { return array[index]; }
        @Override public float floatValue(int index) { return array[index]; }
        @Override public double doubleValue(int index) { return array[index]; }
    }

    private static class LongArray implements NumArray {

        private long[] array;

        LongArray(long[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public boolean isFloating() { return false; }
        @Override public int intValue(int index) { return (int) array[index]; }
        @Override public long longValue(int index) { return array[index]; }
        @Override public float floatValue(int index) { return array[index]; }
        @Override public double doubleValue(int index) { return array[index]; }
    }

    private static class FloatArray implements NumArray {

        private float[] array;

        FloatArray(float[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public boolean isFloating() { return true; }
        @Override public int intValue(int index) { return (int) array[index]; }
        @Override public long longValue(int index) { return (long) array[index]; }
        @Override public float floatValue(int index) { return array[index]; }
        @Override public double doubleValue(int index) { return array[index]; }
    }

    private static class DoubleArray implements NumArray {

        private double[] array;

        DoubleArray(double[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public boolean isFloating() { return true; }
        @Override public int intValue(int index) { return (int) array[index]; }
        @Override public long longValue(int index) { return (long) array[index]; }
        @Override public float floatValue(int index) { return (float) array[index]; }
        @Override public double doubleValue(int index) { return array[index]; }
    }

    private interface StrArray {

        int size();

        String stringValue(int index);
    }

    private static class StringArray implements StrArray {

        private String[] array;

        StringArray(String[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public String stringValue(int index) {
            return array[index];
        }
    }

    private static class CharacterArray implements StrArray {

        private char[] array;

        CharacterArray(char[] array) {
            this.array = array;
        }

        @Override public int size() {
            return array.length;
        }

        @Override public String stringValue(int index) {
            return Character.toString(array[index]);
        }
    }

    // a list of char arrays
    private static class CharArrayListArray implements StrArray {

        private DataList list;

        /**
         * @param list a list of char[]'s
         */
        public CharArrayListArray(DataList list) {
            this.list = list;
        }

        @Override public int size() {
            return list.size();
        }

        @SuppressWarnings("ConstantConditions")
        @Override public String stringValue(int index) {
            // ok... this look kind of dangerous,
            // but we know this is a list of char[]'s and char[]'s coerce to String's.
            return list.getString(index).get();
        }
    }
}
