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
public class MemoryDataMap extends AbstractDataView<String> implements DataMap {

    protected final Map<String, Object> map = Maps.newLinkedHashMap();
    private final DataContainer container;
    @Nullable
    private final DataView parent;
    private final DataView.SafetyMode safety;

    protected MemoryDataMap(DataView.SafetyMode safety) {
        checkState(this instanceof DataContainer, "Cannot construct a root MemoryDataView without a container!");
        this.parent = null;
        this.container = (DataContainer) this;
        this.safety = checkNotNull(safety, "Safety mode");
    }

    protected MemoryDataMap(DataView parent, DataView.SafetyMode safety) {
        this.parent = parent;
        this.container = parent.getContainer();
        this.safety = checkNotNull(safety, "Safety mode");
    }

    @Override
    public String key(String key) {
        return key;
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

        @Nullable final Object object = this.map.get(key);
        if (object == null) {
            return Optional.empty();
        }
        if(this.safety == SafetyMode.ALL_DATA_CLONED) {
            if (object.getClass().isArray()) {
                if (object instanceof boolean[]) {
                    return Optional.of(((boolean[]) object).clone());
                } else if (object instanceof byte[]) {
                    return Optional.of(((byte[]) object).clone());
                } else if (object instanceof short[]) {
                    return Optional.of(((short[]) object).clone());
                } else if (object instanceof int[]) {
                    return Optional.of(((int[]) object).clone());
                } else if (object instanceof long[]) {
                    return Optional.of(((long[]) object).clone());
                } else if (object instanceof float[]) {
                    return Optional.of(((float[]) object).clone());
                } else if (object instanceof double[]) {
                    return Optional.of(((double[]) object).clone());
                } else {
                    return Optional.of(((Object[]) object).clone());
                }
            }
        }
        return Optional.of(object);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public DataMap set(String key, Object value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        boolean copy = this.safety != SafetyMode.NO_DATA_CLONED;

        if (isPrimitive(value)) { // Primitive Allowed Types
            this.map.put(key, value);

        } else if (value instanceof boolean[]) { // Array Allowed Types
            this.map.put(key, copy ? ((boolean[]) value).clone() : value);
        } else if (value instanceof byte[]) {
            this.map.put(key, copy ? ((byte[])    value).clone() : value);
        } else if (value instanceof short[]) {
            this.map.put(key, copy ? ((short[])   value).clone() : value);
        } else if (value instanceof int[]) {
            this.map.put(key, copy ? ((int[])     value).clone() : value);
        } else if (value instanceof long[]) {
            this.map.put(key, copy ? ((long[])    value).clone() : value);
        } else if (value instanceof float[]) {
            this.map.put(key, copy ? ((float[])   value).clone() : value);
        } else if (value instanceof double[]) {
            this.map.put(key, copy ? ((double[])  value).clone() : value);

        } else if (value instanceof DataMap) { // Structure Allowed Types
            copyDataMap(key, (DataMap) value);
        } else if (value instanceof DataList) { // Structure Allowed Types
            copyDataList(key, (DataList) value);
        } else if (value instanceof Collection) {
            setCollection(key, (Collection) value);

        } else if (value instanceof DataSerializable) {
            copyDataMap(key, ((DataSerializable) value).toContainer());

        } else if (value instanceof CatalogType) {
            this.map.put(key, ((CatalogType) value).getId());

        } else if (value instanceof Map) {
            setMap(key, (Map) value);

        } else {
            Optional<? extends DataTranslator> translator = Sponge.getDataManager().getTranslator(value.getClass());
            if (translator.isPresent()) {
                copyDataMap(key, translator.get().translate(value));
            } else {
                throw new IllegalArgumentException(value.getClass() + " can not be serialized");
            }
        }
        return this;
    }

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setCollection(String key, Collection<?> value) {
        DataList sublist = this.createList(key);
        for (Object object : value) {
            sublist.add(object);
        }
    }

    private void setMap(String key, Map<?, ?> value) {
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
        checkArgument(!value.equals(this), "Cannot insert self-referencing Objects!");

        DataList sublist = this.createList(key);
        for (int i = 0; i < value.size(); i++) {
            sublist.add(value.get(i));
        }
    }

    @Override
    public DataMap remove(String key) {
        checkNotNull(key, "key");
        this.map.remove(key);
        return this;
    }

    @Override
    public DataMap createMap(String key) {
        checkNotNull(key, "key");

        DataMap result = new MemoryDataMap(this, this.safety);
        this.map.put(key, result);
        return result;
    }

    @Override
    public DataList createList(String key) {
        checkNotNull(key, "key");

        DataList result = new MemoryDataList(this, this.safety);
        this.map.put(key, result);
        return result;
    }

    @Override
    public DataContainer copy() {
        final DataContainer container = new MemoryDataContainer(this.safety);
        getKeys()
                .forEach(key ->
                        get(key).ifPresent(obj ->
                                container.set(key, obj)
                        )
                );
        return container;
    }

    @Override
    public DataContainer copy(SafetyMode safety) {
        final DataContainer container = new MemoryDataContainer(safety);
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
    public SafetyMode getSafetyMode() {
        return this.safety;
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
        helper.add("safety", this.safety.name());
        return helper.add("map", this.map).toString();
    }
}
