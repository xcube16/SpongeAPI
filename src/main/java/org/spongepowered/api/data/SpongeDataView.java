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
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.util.Coerce;

import java.util.Optional;

import javax.annotation.Nonnull;

public interface SpongeDataView<K> extends DataView<K>{

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
        return this.get(key).flatMap(o -> this.coerseSpongeObject(o, type));
    }

    //TODO: move to Coerce?
    @SuppressWarnings("unchecked")
    default <T extends DataSerializable> Optional<T> coerseSpongeObject(@Nonnull Object obj, @Nonnull Class<T> type) {
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
}
