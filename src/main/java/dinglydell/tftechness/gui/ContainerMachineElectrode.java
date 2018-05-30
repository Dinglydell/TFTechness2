package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import blusunrize.immersiveengineering.common.gui.IESlot;
import dinglydell.tftechness.tileentities.TileMachineElectrode;

public class ContainerMachineElectrode extends ContainerMachineInventory {

	public ContainerMachineElectrode(InventoryPlayer player,
			TileMachineElectrode tile) {
		super(player, tile);
		addSlotToContainer(new IESlot.ArcElectrode(this, tile, 0, 154, 8));

		addSlotToContainer(new IESlot.ArcElectrode(this, tile, 1, 154, 42));

	}

}
