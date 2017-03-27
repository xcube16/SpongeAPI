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
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataTranslator;

import java.util.Optional;

public abstract class SpongeDataQueryable<K> extends DataQueryable<K> implements SpongeDataView<K> {

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
}
