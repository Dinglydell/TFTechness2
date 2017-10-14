package dinglydell.tftechness.tileentities;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.tfc.TileEntities.TEMetalSheet;

public class TETFTMetalSheet extends TEMetalSheet {
	public String metal;

	public TETFTMetalSheet() {
		metal = "Tin";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		metal = nbt.getString("metal");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("metal", metal);
	}

	@Override
	public void handleInitPacket(NBTTagCompound nbt) {
		super.handleInitPacket(nbt);
		metal = nbt.getString("metal");
	}

	@Override
	public void createInitNBT(NBTTagCompound nbt) {
		super.createInitNBT(nbt);
		nbt.setString("metal", this.metal);
	}

}
