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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataTranslator;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Implements implementation independent details of DataList.
 */
public abstract class AbstractDataList extends AbstractDataView<Integer> implements DataList {

    /**
     * An internal method that sets a raw value in the underlying data structure.
     * The key and value are already be sanitized and ready to go.
     */
    protected abstract void setRaw(Integer index, Object value);

    /**
     * An internal method that adds a raw value in the underlying data structure.
     * The value is already be sanitized and ready to go.
     */
    protected abstract void addRaw(Object value);

    @Override
    @SuppressWarnings("unchecked")
    public DataList set(Integer key, Object value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        if (isPrimitiveArray(value)) { // Array Allowed Types
            this.setRaw(key, value);

        } else if (value instanceof DataMap) { // Structure Allowed Types
            copyDataMap(key, (DataMap) value);
        } else if (value instanceof DataList) { // Structure Allowed Types
            copyDataList(key, (DataList) value);

        } else if (value instanceof DataSerializable) { // Sponge Object
            ((DataSerializable) value).toContainer(createMap(key));
        } else if (value instanceof CatalogType) { // Sponge Object
            this.setRaw(key, ((CatalogType) value).getId());

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

    @Override
    public DataList add(Object value) {
        return this.set(this.size(), value);
    }

    /*
     * ===========================
     * ==== queryable methods ====
     * ===========================
     */

    private static final Integer INVALID_KEY = -1;

    @Override
    public Integer key(String key) {
        try {
            return Integer.valueOf(key);
        } catch (NumberFormatException e) {
            return INVALID_KEY;
        }
    }

    @Override
    public DataList set(DataQuery path, Object value) {
        super.set(path, value);
        return this;
    }

    @Override
    public DataList remove(DataQuery path) {
        super.remove(path);
        return this;
    }
}