package dinglydell.tftechness.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class TFTGuiHandler implements IGuiHandler {
	public enum TFTGuis {
		Electrolyser
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == TFTGuis.Electrolyser.ordinal()) {
			return new ContainerElectrolyser();
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == TFTGuis.Electrolyser.ordinal()) {
			return new GuiElectrolyser();
		}
		return null;
	}

}
