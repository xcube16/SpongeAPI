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
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.value.BaseValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A {@link DataView} that stores values like a {@link List}.
 *
 * <p>Primitives (like {@link Integer}, {@link Boolean}, {@link Double}, ...)
 * are NOT supported in {@link DataList}.<p/>
 */
public interface DataList extends DataView<Integer> {

    /**
     * Adds an element to the end of the list.
     *
     * <p>The element must be one of<br/>
     * * Structure Allowed Types<br/>
     * * Array Allowed Types
     * * {@link DataSerializable}<br/>
     * * {@link CatalogType}<br/>
     * * have a {@link DataTranslator} registered in Sponge's {@link DataManager}<br/>
     * * {@link Map} (will be coerced into a DataMap, or error on failure)<br/>
     * * {@link Collection} (will be coerced into a {@link DataList}/array, or error on failure)</p>
     *
     * @param element The element to add
     * @return This list, for chaining
     * @throws IllegalArgumentException thrown when {@code element} is of an unsupported type
     */
    DataList add(Object element);

    /**
     * Creates a new {@link DataMap} and adds it to the end of the list.
     *
     * @return The newly created {@link DataMap}
     */
    DataMap addMap();

    /**
     * Creates a new {@link DataList} and adds it to the end of the list.
     *
     * @return The newly created {@link DataList}
     */
    DataList addList();

    @Override
    DataList set(Integer index, Object element);

    @Override
    DataList set(DataQuery query, Object value);

    @Override
    default <E> DataList set(Key<? extends BaseValue<E>> key, E value) {
        return this.set(checkNotNull(key, "Key was null!").getQuery(), value);
    }

    @Override
    DataList remove(Integer index);

    @Override
    DataList remove(DataQuery path);
}
