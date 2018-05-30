package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class ContainerMachine extends Container {
	protected TileMachineComponent tile;

	public ContainerMachine(InventoryPlayer player, TileMachineComponent tile) {
		this.tile = tile;

		//player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(player, x + y * 9 + 9,
						8 + x * 18, 83 + y * 18));
			}
		}
		//player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 141));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tile.isUseableByPlayer(player);
	}

}
