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

import java.util.Map;
import java.util.Set;

public interface DataMap extends DataView<String> {

    /**
     * Gets a collection containing all keys in this {@link DataView}.
     *
     * <p>If deep is set to true, then this will contain all the keys
     * within any child {@link DataView}s (and their children, etc).
     * These will be in a valid path notation for you to use.</p>
     *
     * <p>If deep is set to false, then this will contain only the keys
     * of any direct children, and not their own children.</p>
     *
     * @param deep Whether or not to get all children keys
     * @return A set of current keys in this container
     */
    Set<DataQuery> getKeys(boolean deep);

    /**
     * Gets a Map containing all keys and their values for this {@link DataView}.
     *
     * <p>If deep is set to true, then this will contain all the keys and
     * values within any child {@link DataView}s (and their children,
     * etc). These keys will be in a valid path notation for you to use.</p>
     *
     * <p>If deep is set to false, then this will contain only the keys and
     * values of any direct children, and not their own children.</p>
     *
     * @param deep Whether or not to get a deep list of all children or not
     * @return Map of keys and values of this container
     */
    Map<DataQuery, Object> getValues(boolean deep);

}
