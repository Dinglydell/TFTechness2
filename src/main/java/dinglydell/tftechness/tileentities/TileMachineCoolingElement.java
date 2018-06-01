package dinglydell.tftechness.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.TFTechness2;

public class TileMachineCoolingElement extends TileMachineRF {

	private static final float RF_TO_COOLANT = 0.0005f;
	/** The heat transfered into the cooler is then multipled by this */
	private static final float EXCESS_HEAT_MULTIPLIER = 1.1f;
	/** if true, the machine will attempt to cool machines on this side */
	private boolean[] isSideCool = new boolean[6];

	public TileMachineCoolingElement() {
		isSideCool[0] = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {

		super.writeToNBT(data);
		NBTTagList coolTag = new NBTTagList();

		for (int i = 0; i < isSideCool.length; ++i) {
			NBTTagCompound sideTag = new NBTTagCompound();
			sideTag.setByte("Side", (byte) i);
			sideTag.setBoolean("Value", isSideCool[i]);
			coolTag.appendTag(sideTag);
		}

		data.setTag("IsCool", coolTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {

		super.readFromNBT(data);
		NBTTagList coolTag = data.getTagList("IsCool", 10);
		for (int i = 0; i < coolTag.tagCount(); ++i) {
			NBTTagCompound sideTag = coolTag.getCompoundTagAt(i);
			byte side = sideTag.getByte("Side");
			isSideCool[i] = sideTag.getBoolean("Value");
		}
	}

	@Override
	protected void writePacketNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		super.writePacketNBT(nbt);
		NBTTagList coolTag = new NBTTagList();

		for (int i = 0; i < isSideCool.length; ++i) {
			NBTTagCompound sideTag = new NBTTagCompound();
			sideTag.setByte("Side", (byte) i);
			sideTag.setBoolean("Value", isSideCool[i]);
			coolTag.appendTag(sideTag);
		}

		nbt.setTag("IsCool", coolTag);
	}

	@Override
	protected void readPacketNBT(NBTTagCompound nbt) {
		super.readPacketNBT(nbt);
		NBTTagList coolTag = nbt.getTagList("IsCool", 10);
		for (int i = 0; i < coolTag.tagCount(); ++i) {
			NBTTagCompound sideTag = coolTag.getCompoundTagAt(i);
			byte side = sideTag.getByte("Side");
			isSideCool[i] = sideTag.getBoolean("Value");
		}
	}

	@Override
	protected int spendRF(int spend) {
		for (int i = 0; i < isSideCool.length; i++) {

			if (!isSideCool[i]) {
				continue;
			}
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			ForgeDirection dir = ForgeDirection.values()[(i + meta) % 6];
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
					yCoord + dir.offsetY,
					zCoord + dir.offsetZ);
			if (tile != null && tile instanceof TileMachineComponent) {
				TileMachineComponent tc = (TileMachineComponent) tile;
				float oldTemp = tc.temperature;
				tc.temperature = Math.max(TFTechness2.ABSOLUTE_ZERO,
						tc.temperature - spend * RF_TO_COOLANT
								* tier.efficiency);
				temperature += EXCESS_HEAT_MULTIPLIER
						* (oldTemp - tc.temperature);
			}
		}
		//temperature = Math.max(TFTechness2.ABSOLUTE_ZERO, temperature - spend
		//	* RF_TO_COOLANT * tier.efficiency);
		return spend;
	}

	@Override
	protected void setPlacedSide(int side) {

		//isSideCool[side] = true;

	}

	@Override
	public IIcon getIcon(IIcon[][] icons, int side) {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return component.getIcon(isSideCool[(6 + side - meta) % 6] ? 1 : 0);
	}

}
