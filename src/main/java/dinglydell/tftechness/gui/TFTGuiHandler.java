package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import dinglydell.tftechness.tileentities.TileTFTElectrolyser;

public class TFTGuiHandler implements IGuiHandler {
	public enum TFTGuis {
		Electrolyser
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == TFTGuis.Electrolyser.ordinal()) {
			return new ContainerElectrolyser(player.inventory,
					(TileTFTElectrolyser) world.getTileEntity(x, y, z));
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
		return null;
	}

}
