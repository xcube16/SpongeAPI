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
 * Implements implementation independent details of DataMap.
 */
public abstract class AbstractDataMap extends AbstractDataView<String> implements DataMap {


    /**
     * An internal method that sets a raw value in the underlying data structure.
     * The key and value are already be sanitized and ready to go.
     */
    protected abstract void setRaw(String key, Object value);

    @Override
    @SuppressWarnings("unchecked")
    public DataMap set(String key, Object value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        if (isPrimitive(value) || isPrimitiveArray(value)) { // Primitive Allowed Types or Array Allowed Types
            this.setRaw(key, value);

        } else if (value instanceof DataMap) { // Structure Allowed Types
            copyDataMap(key, (DataMap) value);
        } else if (value instanceof DataList) { // Structure Allowed Types
            copyDataList(key, (DataList) value);

        } else if (value instanceof DataSerializable) { // Sponge Object
            ((DataSerializable) value).toContainer(this.createMap(key));
        } else if (value instanceof CatalogType) { // Sponge Object
            this.setRaw(key, ((CatalogType) value).getId());

        } else if (value instanceof Enum) { // common java stuff
            this.setRaw(key, ((Enum) value).name());
        } else if (value instanceof Map) { // common java stuff
            copyMap(key, (Map) value);
        } else if (value instanceof Collection) { // common java stuff
            copyCollection(key, (Collection) value);

        } else { // Sponge Object? maybe?
            Optional<? extends DataTranslator> translator = Sponge.getDataManager().getTranslator(value.getClass());
            if (translator.isPresent()) { // yep, Sponge Object
                translator.get().translate(value, createMap(key));
            } else { // nope, KU-BOOM!
                throw new IllegalArgumentException(value.getClass() + " can not be serialized");
            }
        }
        return this;
    }

    /*
     * ===========================
     * ==== queryable methods ====
     * ===========================
     */

    @Override
    public String key(String key) {
        return key;
    }

    @Override
    public DataMap set(DataQuery path, Object value) {
        super.set(path, value);
        return this;
    }

    @Override
    public DataMap remove(DataQuery path) {
        super.remove(path);
        return this;
    }
}
