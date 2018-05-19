package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;

public class ContainerMachineShelf extends ContainerMachine {

	private TileMachineComponentItemShelf tile;

	public ContainerMachineShelf(InventoryPlayer player,
			TileMachineComponentItemShelf tile) {
		super(player, tile);
		this.tile = tile;
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(tile, i, 62 + i % 3 * 18,
					7 + i / 3 * 18));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot) {
		ItemStack prev = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			prev = stack.copy();
			TFTechness2.logger.info(fromSlot);
			if (slot.inventory == tile) {
				mergeItemStack(stack,
						tile.getSizeInventory(),
						this.inventorySlots.size() - 1,
						true);
			} else {

				for (int i = 0; i < tile.getSizeInventory(); i++) {
					if (tile.isItemValidForSlot(i, stack)
							&& tile.getStackInSlot(i) == null) {
						tile.setInventorySlotContents(i, slot.decrStackSize(1));
						if (!slot.getHasStack()) {
							break;
						}
					}
				}
			}
			if (stack.stackSize == prev.stackSize) {
				return null;
			}
		}
		return prev;
	}

}
