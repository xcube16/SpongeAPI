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

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;

import java.util.Map;
import java.util.Set;

/**
 * A {@link DataView} that stores values like a {@link Map}.
 *
 * <p>Primitives (like {@link Integer}, {@link Boolean}, {@link Double}, ...)
 * are NOT supported in {@link DataList}.<p/>
 */
public interface DataMap extends DataView<String> {

    /**
     * Gets a collection containing all keys in this {@link DataMap}.
     *
     * @return A set of current keys in this container
     */
    Set<String> getKeys();

    @Override
    DataList set(String key, Object element);

    @Override
    DataMap set(DataQuery query, Object value);

    @Override
    default <E> DataMap set(Key<? extends BaseValue<E>> key, E value) {
        return this.set(checkNotNull(key, "Key was null!").getQuery(), value);
    }

    @Override
    DataMap remove(String key);

    @Override
    DataMap remove(DataQuery path);
}
