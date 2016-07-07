package org.spongepowered.api.event.item.inventory;

import org.spongepowered.api.item.inventory.Slot;

public interface TargetSlotEvent extends TargetInventoryEvent {
    @Override
    Slot getTargetInventory();
}
