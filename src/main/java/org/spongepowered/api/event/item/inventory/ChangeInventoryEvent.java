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
package org.spongepowered.api.event.item.inventory;

import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.item.TargetItemEvent;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

/**
 * Base event for all changes that affect {@link Inventory}s.
 */
public interface ChangeInventoryEvent extends TargetInventoryEvent, Cancellable {

    /**
     * Fired when a {@link Living} changes it's equipment.
     */
    interface Equipment extends ChangeInventoryEvent, TargetSlotEvent {
    }

    /**
     * Fired when a {@link Living} changes it's held {@link ItemStack}.
     */
    interface Held extends ChangeInventoryEvent, TargetSlotEvent {
    }

    /**
     * Fired when an {@link ItemStack} is transferred from one {@link Slot} to another in a {@link Container}.
     */
    interface Transfer extends ChangeInventoryEvent, TargetContainerEvent, AffectSlotEvent {
    }

    /**
     * Fired when an {@link Item} is to be "pickup'd" into a {@link Slot} as an {@link ItemStack}.
     */
    interface Pickup extends ChangeInventoryEvent, TargetItemEvent, TargetSlotEvent {
    }
}
