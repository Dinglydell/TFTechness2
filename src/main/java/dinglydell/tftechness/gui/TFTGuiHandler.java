package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineComponentItemShelf;
import dinglydell.tftechness.tileentities.TileMachineComponentTank;
import dinglydell.tftechness.tileentities.TileMachineComponentTurbine;
import dinglydell.tftechness.tileentities.TileMachineElectrode;
import dinglydell.tftechness.tileentities.TileMachineFirebox;
import dinglydell.tftechness.tileentities.TileMachineMonitor;

public class TFTGuiHandler implements IGuiHandler {
	public enum TFTGuis {
		Machine, ItemShelf, Tank, Electrode, Monitor, Turbine, Firebox
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		//if (ID == TFTGuis.Electrolyser.ordinal()) {
		//	return new ContainerElectrolyser(player.inventory,
		//			(TileTFTElectrolyser) world.getTileEntity(x, y, z));
		//}
		if (ID == TFTGuis.Machine.ordinal()) {
			return new ContainerMachine(player.inventory,
					(TileMachineComponent) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.ItemShelf.ordinal()) {
			return new ContainerMachineShelf(player.inventory,
					(TileMachineComponentItemShelf) world
							.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Tank.ordinal()) {
			return new ContainerMachineTank(player.inventory,
					(TileMachineComponentTank) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Electrode.ordinal()) {
			return new ContainerMachineElectrode(player.inventory,
					(TileMachineElectrode) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Monitor.ordinal()) {
			return new ContainerMonitor(player.inventory,
					(TileMachineMonitor) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Turbine.ordinal()) {
			return new ContainerMachineTank(player.inventory,
					(TileMachineComponentTurbine) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Firebox.ordinal()) {
			return new ContainerFirebox(player.inventory,
					(TileMachineFirebox) world.getTileEntity(x, y, z));
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		//if (ID == TFTGuis.Electrolyser.ordinal()) {
		//	return new GuiElectrolyser(player.inventory,
		//			(TileTFTElectrolyser) world.getTileEntity(x, y, z));
		//}
		if (ID == TFTGuis.Machine.ordinal()) {
			return new GuiMachine(player.inventory,
					(TileMachineComponent) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.ItemShelf.ordinal()) {
			return new GuiMachineShelf(player.inventory,
					(TileMachineComponentItemShelf) world
							.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Tank.ordinal()) {
			return new GuiMachineTank(player.inventory,
					(TileMachineComponentTank) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Electrode.ordinal()) {
			return new GuiMachineElectrode(player.inventory,
					(TileMachineElectrode) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Monitor.ordinal()) {
			return new GuiMonitor(player.inventory,
					(TileMachineMonitor) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Turbine.ordinal()) {
			return new GuiMachineTurbine(player.inventory,
					(TileMachineComponentTurbine) world.getTileEntity(x, y, z));
		}
		if (ID == TFTGuis.Firebox.ordinal()) {
			return new GuiFirebox(player.inventory,
					(TileMachineFirebox) world.getTileEntity(x, y, z));
		}
		return null;
	}
}
