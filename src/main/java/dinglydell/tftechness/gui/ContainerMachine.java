package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class ContainerMachine extends Container {
	private TileMachineComponent tile;

	public ContainerMachine(InventoryPlayer player, TileMachineComponent tile) {
		this.tile = tile;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tile.isUseableByPlayer(player);
	}
}
