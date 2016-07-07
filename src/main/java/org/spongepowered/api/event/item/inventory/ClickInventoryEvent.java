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

import org.spongepowered.api.item.inventory.Slot;

public interface ClickInventoryEvent extends ChangeInventoryEvent {

    /**
     * Fired when performing a click on a {@link Slot} with the primary click mouse button.
     */
    interface Primary extends ClickInventoryEvent, TargetSlotEvent {}

    /**
     * Fired when performing a click on a {@link Slot}  with the middle click mouse button.
     */
    interface Middle extends ClickInventoryEvent, TargetSlotEvent {}

    /**
     * Fired when performing a click on a {@link Slot}  with the secondary mouse button.
     */
    interface Secondary extends ClickInventoryEvent, TargetSlotEvent {}

    /**
     * Fired when performing any type of click in creative mode.
     */
    interface Creative extends ClickInventoryEvent {}

    interface Shift extends ClickInventoryEvent, AffectSlotEvent {

        interface Primary extends Shift, ClickInventoryEvent.Primary {}

        interface Secondary extends Shift, ClickInventoryEvent.Secondary {}
    }

    /**
     * Fired when performing a double click on a {@link Slot}.
     */
    interface Double extends ClickInventoryEvent.Primary {}

    interface Drop extends ClickInventoryEvent, DropItemEvent.Dispense {

        interface Single extends Drop {}

        interface Full extends Drop {}

        interface Outside extends Drop {

            interface Primary extends Outside, ClickInventoryEvent.Primary {}

            interface Secondary extends Outside, ClickInventoryEvent.Secondary {}
        }
    }

    interface Drag extends ClickInventoryEvent, AffectSlotEvent {

        interface Primary extends Drag, ClickInventoryEvent.Primary {}

        interface Secondary extends Drag, ClickInventoryEvent.Secondary {}
    }

    interface NumberPress extends ClickInventoryEvent {
        
        int getNumber();
    }
}
