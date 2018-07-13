package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import dinglydell.tftechness.tileentities.TileMachineFirebox;

public class ContainerFirebox extends ContainerMachineInventory {

	public ContainerFirebox(InventoryPlayer player, TileMachineFirebox tile) {
		super(player, tile);
		addSlotToContainer(new SlotFuel(tile, 0, 80, 42));
		for (int i = 1; i < tile.getSizeInventory(); i++) {
			addSlotToContainer(new SlotFuel(tile, i, 152, 78 - i * 18));
		}
	}

}
