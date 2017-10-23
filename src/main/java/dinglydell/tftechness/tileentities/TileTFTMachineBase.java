package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.multiblock.IMultiblockTFT;
import dinglydell.tftechness.network.PacketTFTMachine;
import dinglydell.tftechness.util.ItemUtil;

public abstract class TileTFTMachineBase extends TileEntity implements
		IInventory, IEnergyReceiver {
	private int masterX, masterY, masterZ;
	private EnumFacing facing;
	protected int rf;

	protected ItemStack[] inventory;

	//public TileTFTMachineBase(World world){

	//}

	@Override
	public void updateEntity() {
		if (isMaster() && !worldObj.isRemote && rf > 0) {
			rf -= spendRf(Math.min(getMaxRfRate(), rf));
			this.sendServerToClientMessage();
		}
	}

	protected abstract int getMaxRfRate();

	protected abstract int spendRf(int amt);

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("masterX", masterX);
		data.setInteger("masterY", masterY);
		data.setInteger("masterZ", masterZ);

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
		return xCoord == masterX && yCoord == masterY && zCoord == masterZ;
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
		if (isMaster()) {
			return rf;
		}
		return getMaster().getEnergyStored(direction);
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
		TileTFTMachineBase master = getMaster();
		if (master == null) {
			return 0;
		}
		return master.receiveEnergy(direction, amt, simulated);

	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writePacketNBT(nbt);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 3, nbt);

	}

	protected void writePacketNBT(NBTTagCompound nbt) {
		nbt.setInteger("masterX", masterX);
		nbt.setInteger("masterY", masterY);
		nbt.setInteger("masterZ", masterZ);
		if (isMaster()) {
			writeToMasterNBT(nbt);
		}

	}

	private TileTFTMachineBase getMaster() {
		return (TileTFTMachineBase) worldObj.getTileEntity(masterX,
				masterY,
				masterZ);
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		//if(worldObj.isRemote){
		this.readPacketNBT(packet.func_148857_g());
		//}
	}

	protected void readPacketNBT(NBTTagCompound nbt) {
		masterX = nbt.getInteger("masterX");
		masterY = nbt.getInteger("masterY");
		masterZ = nbt.getInteger("masterZ");
		if (isMaster()) {
			readFromMasterNBT(nbt);
		}

	}

	@Override
	public boolean canConnectEnergy(ForgeDirection direction) {
		//TODO: restrict this?
		return true;
	}

	/**
	 * Sends a message from the client to the server. Used when the player does
	 * something in a GUI
	 */
	public void sendClientToServerMessage() {
		TFTechness2.snw.sendToServer(new PacketTFTMachine(this, Side.CLIENT));
	}

	/**
	 * Read a message from the client. Call when the player does something in a
	 * GUI
	 */
	public void readClientToServerMessage(NBTTagCompound nbt) {
		markDirty();
	}

	public abstract void writeClientToServerMessage(NBTTagCompound nbt);

	/**
	 * Message to send to client for small, regular updates (such as amount of
	 * RF)
	 */
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		nbt.setInteger("rf", rf);
	}

	public void readServerToClientMessage(NBTTagCompound nbt) {
		rf = nbt.getInteger("rf");

	}

	private void sendServerToClientMessage() {
		TFTechness2.snw
				.sendToAllAround(new PacketTFTMachine(this, Side.SERVER),
						new TargetPoint(worldObj.provider.dimensionId, xCoord,
								yCoord, zCoord, 64));

	}

}
