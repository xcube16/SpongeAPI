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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataTranslator;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Default implementation of a {@link DataView} being used in memory.
 */
public class MemoryDataMap extends AbstractDataMap {

    protected final Map<String, Object> map = Maps.newLinkedHashMap();
    private final DataContainer container;
    @Nullable
    private final DataView parent;

    protected MemoryDataMap() {
        checkState(this instanceof DataContainer, "Cannot construct a root MemoryDataView without a container!");
        this.parent = null;
        this.container = (DataContainer) this;
    }

    protected MemoryDataMap(DataView parent) {
        this.parent = parent;
        this.container = parent.getContainer();
    }

    @Override
    public DataContainer getContainer() {
        return this.container;
    }

    @Override
    public Optional<DataView> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public Set<String> getKeys() {
        return this.map.keySet();
    }

    @Override
    public Optional<Object> get(String key) {
        checkNotNull(key, "key");

        return Optional.ofNullable(this.map.get(key));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public MemoryDataMap set(String key, Object value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        if (isPrimitive(value) || isPrimitiveArray(value)) { // Primitive Allowed Types or Array Allowed Types
            this.map.put(key, value);

        } else if (value instanceof DataMap) { // Structure Allowed Types
            copyDataMap(key, (DataMap) value);
        } else if (value instanceof DataList) { // Structure Allowed Types
            copyDataList(key, (DataList) value);

        } else if (value instanceof DataSerializable) { // Sponge Object
            copyDataMap(key, ((DataSerializable) value).toContainer());
        } else if (value instanceof CatalogType) { // Sponge Object
            this.map.put(key, ((CatalogType) value).getId());

        } else if (value instanceof Map) { // just extra candy
            copyMap(key, (Map) value);
        } else if (value instanceof Collection) { // just extra candy
            copyCollection(key, (Collection) value);

        } else { // Sponge Object? maybe?
            Optional<? extends DataTranslator> translator = Sponge.getDataManager().getTranslator(value.getClass());
            if (translator.isPresent()) { // yep, Sponge Object
                copyDataMap(key, translator.get().translate(value));
            } else { // nope, KU-BOOM!
                throw new IllegalArgumentException(value.getClass() + " can not be serialized");
            }
        }
        return this;
    }

    /**
     * is a Primitive Allowed Type
     */
    private boolean isPrimitive(Object value) {
         return value instanceof Boolean ||
                value instanceof Byte ||
                value instanceof Character ||
                value instanceof Short ||
                value instanceof Integer ||
                value instanceof Long ||
                value instanceof Float ||
                value instanceof Double ||
                value instanceof String;
    }

    /**
     * is an Array Allowed Type
     */
    private boolean isPrimitiveArray(Object value) {
        return value instanceof boolean[] ||
                value instanceof byte[] ||
                value instanceof String ||
                value instanceof short[] ||
                value instanceof int[] ||
                value instanceof long[] ||
                value instanceof float[] ||
                value instanceof double[];
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void copyCollection(String key, Collection<?> value) {
        DataList sublist = this.createList(key);
        for (Object object : value) {
            sublist.add(object);
        }
    }

    private void copyMap(String key, Map<?, ?> value) {
        DataMap submap = this.createMap(key);
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            if (entry.getKey() instanceof String) {
               submap.set((String) entry.getKey(), entry.getValue());
            } else {
                throw new IllegalArgumentException("map had an unsupported key type");
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void copyDataMap(String key, DataMap value) {
        checkArgument(!value.equals(this), "Cannot insert self-referencing Objects!");

        DataMap submap = this.createMap(key);
        for (String subkey : value.getKeys()) {
            submap.set(subkey, value.get(subkey).get());
        }
    }

    private void copyDataList(String key, DataList value) {
        DataList sublist = this.createList(key);
        for (int i = 0; i < value.size(); i++) {
            sublist.add(value.get(i));
        }
    }

    @Override
    public MemoryDataMap remove(String key) {
        checkNotNull(key, "key");
        this.map.remove(key);
        return this;
    }

    @Override
    public DataMap createMap(String key) {
        checkNotNull(key, "key");

        DataMap result = new MemoryDataMap(this);
        this.map.put(key, result);
        return result;
    }

    @Override
    public DataList createList(String key) {
        checkNotNull(key, "key");

        DataList result = new MemoryDataList(this);
        this.map.put(key, result);
        return result;
    }

    @Override
    public DataContainer copy() {
        final DataContainer container = new MemoryDataContainer();
        getKeys()
                .forEach(key ->
                        get(key).ifPresent(obj ->
                                container.set(key, obj)
                        )
                );
        return container;
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MemoryDataMap other = (MemoryDataMap) obj;

        return Objects.equal(this.map.entrySet(), other.map.entrySet());
    }

    @Override
    public String toString() {
        final Objects.ToStringHelper helper = Objects.toStringHelper(this);
        return helper.add("map", this.map).toString();
    }
}
