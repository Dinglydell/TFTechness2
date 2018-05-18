package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;

public class TFTGuiHandler implements IGuiHandler {
	public enum TFTGuis {
		Electrolyser, Machine, ItemShelf
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == TFTGuis.Electrolyser.ordinal()) {
			return new ContainerElectrolyser(player.inventory,
					(TileTFTElectrolyser) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Machine.ordinal()) {
			return new ContainerMachine(player.inventory,
					(TileMachineComponent) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.ItemShelf.ordinal()) {
			return new ContainerMachineShelf(player.inventory,
					(TileMachineComponentItemShelf) world
							.getTileEntity(x, y, z));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == TFTGuis.Electrolyser.ordinal()) {
			return new GuiElectrolyser(player.inventory,
					(TileTFTElectrolyser) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Machine.ordinal()) {
			return new GuiMachine(player.inventory,
					(TileMachineComponent) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.ItemShelf.ordinal()) {
			return new GuiMachineShelf(player.inventory,
					(TileMachineComponentItemShelf) world
							.getTileEntity(x, y, z));
		}
		return null;
	}

}
