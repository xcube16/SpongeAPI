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

import java.util.Collection;
import java.util.Optional;

/**
 * Manages structures stored with a world for use with structure blocks.
 */
public interface StructureManager {

    /**
     * Gets if the given structure exists.
     * 
     * @param id The id to check
     * @return True if the structure exists
     */
    boolean exists(String id);

    /**
     * Attempts to load the structure with the given name.
     * 
     * @param id The structure id to load
     * @return The structure, if found
     */
    Optional<Schematic> load(String id);

    /**
     * Saves the given schematic as a structure, overwriting a previous
     * structure if it already existed.
     * 
     * @param id
     * @param structure
     */
    void save(String id, Schematic structure);

    /**
     * Gets all existing structure ids.
     * 
     * @return All existing ids
     */
    Collection<String> getExistingStructures();

}
