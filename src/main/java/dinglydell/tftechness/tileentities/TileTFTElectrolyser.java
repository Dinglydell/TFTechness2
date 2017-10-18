package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;

public class TileTFTElectrolyser extends TileTFTMachineBase {

	@Override
	protected void writeToMasterNBT(NBTTagCompound data) {

	}

	@Override
	protected void readFromMasterNBT(NBTTagCompound data) {

	}

	@Override
	public boolean openGui(World world, EntityPlayer player) {
		if (!hasMaster()) {
			return false;
		}
		player.openGui(TFTechness2.instance,
				TFTGuis.Electrolyser.ordinal(),
				world,
				getMasterX(),
				getMasterY(),
				getMasterZ());
		return true;

	}

}
