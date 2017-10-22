package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyReceiver;
import dinglydell.tftechness.multiblock.IMultiblockTFT;
import dinglydell.tftechness.util.ItemUtil;

public abstract class TileTFTMachineBase extends TileEntity implements
		IInventory, IEnergyReceiver {
	private boolean isMaster;
	private int masterX, masterY, masterZ;
	private EnumFacing facing;
	protected int rf;

	protected ItemStack[] inventory;

	//public TileTFTMachineBase(World world){

	//}

	@Override
	public void updateEntity() {
		rf -= spendRf(Math.min(getMaxRfRate(), rf));
	}

	protected abstract int getMaxRfRate();

	protected abstract int spendRf(int amt);

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("masterX", masterX);
		data.setInteger("masterY", masterY);
		data.setInteger("masterZ", masterZ);
		data.setBoolean("isMaster", isMaster);
		if (isMaster()) {
			writeToMasterNBT(data);
		}
	}

	protected void writeToMasterNBT(NBTTagCompound data) {
		data.setInteger("facing", facing.ordinal());
		data.setInteger("RF", rf);

		//inventory
		NBTTagList invTag = new NBTTagList();

		for (int i = 0; i < inventory.length; ++i) {
			if (inventory[i] != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(itemTag);
				invTag.appendTag(itemTag);
			}
		}

		data.setTag("Items", invTag);
	}

	protected void readFromMasterNBT(NBTTagCompound data) {
		facing = EnumFacing.values()[data.getInteger("facing")];
		rf = data.getInteger("rf");
		//inventory
		NBTTagList invTag = data.getTagList("Items", 10);

		for (int i = 0; i < invTag.tagCount(); ++i) {
			NBTTagCompound itemTag = invTag.getCompoundTagAt(i);
			byte b0 = itemTag.getByte("Slot");

			if (b0 >= 0 && b0 < inventory.length) {
				inventory[b0] = ItemStack.loadItemStackFromNBT(

				itemTag);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		masterX = data.getInteger("masterX");
		masterY = data.getInteger("masterY");
		masterZ = data.getInteger("masterZ");
		isMaster = data.getBoolean("isMaster");
		if (isMaster()) {
			readFromMasterNBT(data);
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if (inventory[slot] == null) {
			return null;
		}
		if (inventory[slot].stackSize <= amt) {
			ItemStack stack = inventory[slot];
			inventory[slot] = null;
			return stack;
		}
		inventory[slot].stackSize -= amt;
		return ItemUtil.clone(inventory[slot], amt);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;

	}

	public boolean isMaster() {
		return isMaster;
	}

	public int getMasterX() {
		return masterX;
	}

	public int getMasterY() {
		return masterY;
	}

	public int getMasterZ() {
		return masterZ;
	}

	public void setIsMaster(boolean bool) {
		isMaster = bool;
	}

	public void setMasterCoords(int x, int y, int z) {
		masterX = x;
		masterY = y;
		masterZ = z;
	}

	public abstract boolean openGui(World world, EntityPlayer player);

	public abstract IMultiblockTFT getMultiblock();

	public EnumFacing getFacing() {
		return facing;
	}

	@Override
	public int getEnergyStored(ForgeDirection direction) {
		return rf;
	}

	@Override
	public int receiveEnergy(ForgeDirection direction, int amt,
			boolean simulated) {
		if (isMaster()) {
			int oldRf = rf;
			int newRf = Math.min(rf + amt, getMaxEnergyStored(direction));
			if (!simulated) {
				rf = newRf;
			}
			return newRf - oldRf;
		}

		return getMaster().receiveEnergy(direction, amt, simulated);

	}

	private TileTFTMachineBase getMaster() {
		return (TileTFTMachineBase) worldObj.getTileEntity(masterX,
				masterY,
				masterZ);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection direction) {
		//TODO: restrict this?
		return true;
	}

}
