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
package org.spongepowered.api.world.schematic;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.extent.ArchetypeVolume;

public interface LocalBlockPalette extends BlockPalette {

    /**
     * Removes the given blockstate from the mapping.
     * 
     * @param state The blockstate to remove
     * @return If the blockstate existed in the mapping
     */
    boolean remove(BlockState state);

    /**
     * Gets if this block palette supports transparency and has a transparent
     * index allocated.
     * 
     * @return True if this block palette supports translarency
     */
    boolean supportsTransparency();

    /**
     * Gets the transparent index for this palette. The transparent index may be
     * used to mark blocks which are ingored when applying an
     * {@link ArchetypeVolume} to a world for example.
     * 
     * @return The transparent index
     * @throws IllegalStateException If this palette does not support
     *         transparency
     * @see #supportsTransparency()
     */
    int getTransparentIndex();

}
