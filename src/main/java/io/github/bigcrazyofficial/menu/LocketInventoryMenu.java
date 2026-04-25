package io.github.bigcrazyofficial.menu;

import io.github.bigcrazyofficial.data.base.BondData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LocketInventoryMenu extends AbstractContainerMenu {
    private final Container container;

    public LocketInventoryMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(5));
    }

    public LocketInventoryMenu(final int containerId, final Inventory inventory, final Container container) {
        super(MenuTypes.LOCKET_INVENTORY, containerId);
        checkContainerSize(container, 5);
        this.container = container;

        container.startOpen(inventory.player);

        int rows = 1;
        int columns = 5;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                this.addSlot(new Slot(container, x, 44 + x * 18, 29 + y * 18));
            }
        }

        // Add the player inventory slots.
        this.addStandardInventorySlots(inventory, 8, 84);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();

        if (slotIndex < container.getContainerSize()) {
            if (!this.moveItemStackTo(stack, container.getContainerSize(), this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, container.getContainerSize(), false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
