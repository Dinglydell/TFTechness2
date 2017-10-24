package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import blusunrize.immersiveengineering.common.gui.IESlot;

import com.bioxx.tfc.Containers.Slots.SlotMoldTool2;

import dinglydell.tftechness.tileentities.TileTFTElectrolyser;

public class ContainerElectrolyser extends Container {

	private TileTFTElectrolyser tile;

	public ContainerElectrolyser(InventoryPlayer player,
			TileTFTElectrolyser tile) {
		this.tile = tile;
		//electrode slots
		addSlotToContainer(new IESlot.ArcElectrode(this, tile,
				TileTFTElectrolyser.Slots.ELECTRODE_A.ordinal(), 155, 9));

		addSlotToContainer(new IESlot.ArcElectrode(this, tile,
				TileTFTElectrolyser.Slots.ELECTRODE_A.ordinal(), 155, 43));

		//input
		addSlotToContainer(new SlotPowder(tile,
				TileTFTElectrolyser.Slots.INPUT.ordinal(), 46, 24));

		////alumina
		//addSlotToContainer(new Slot(tile,
		//		TileTFTElectrolyser.Slots.alumina.ordinal(), 71, 43));
		//mold
		addSlotToContainer(new SlotMoldTool2(tile,
				TileTFTElectrolyser.Slots.MOLD.ordinal(), 134, 43));

		//player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(player, x + y * 9 + 9,
						9 + x * 18, 84 + y * 18));
			}
		}
		//player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(player, x, 9 + x * 18, 142));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return this.tile.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot) {
		ItemStack prev = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			prev = stack.copy();
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				if (tile.isItemValidForSlot(i, stack)
						&& tile.getStackInSlot(i) == null) {
					tile.setInventorySlotContents(i, slot.decrStackSize(1));
					if (!slot.getHasStack()) {
						break;
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
