package dinglydell.tftechness.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

import com.bioxx.tfc.Core.TFC_Climate;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.gui.component.ITileTemperature;
import dinglydell.tftechness.network.PacketMachineComponent;

public/* abstract */class TileMachineComponent extends TileEntity implements
		ITileTemperature, IInventory {
	private static final float AIR_CONDUCTIVITY = 0.005f;
	private int masterX, masterY = -1, masterZ;
	protected float temperature;
	protected float conductivity = 0.5f;

	//private ItemStack[] inventory;

	//protected SolutionTank internalTank;

	public TileMachineComponent() {

	}

	public TileMachineComponent setConductivity(float conductivity) {
		this.conductivity = conductivity;

		return this;
	}

	public void CheckForMaster() {
		CheckForMaster(null);
	}

	public float getTemperature() {
		return this.temperature;
		//return this.temperature / getNetSHMass() - 273;
	}

	/**
	 * Checks all neighbours for a potential controller, and asks neighbours to
	 * check again if necessary
	 * 
	 * @param caller
	 *            The component that asked us to check - we won't ask them to
	 *            check again to avoid an infinite loop of checking
	 */
	private void CheckForMaster(TileMachineComponent caller) {
		List<TileMachineComponent> updateCandidates = new ArrayList<TileMachineComponent>();
		for (ForgeDirection dir : ForgeDirection.values()) {
			if (dir == ForgeDirection.UNKNOWN) {
				continue;
			}
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
					yCoord + dir.offsetY,
					zCoord + dir.offsetZ);
			if (tile != null) {
				if (tile instanceof TileMachineComponent) {
					TileMachineComponent tc = (TileMachineComponent) tile;
					if (tc.hasMaster()) {
						if (!hasMaster()) { // if we don't have a master adopt the neighbour's master
							setMasterCoords(tc.masterX, tc.masterY, tc.masterZ);
						}
						if (!hasSameMaster(tc)) { // if we already had a different master, abandon master (ambiguous)
							removeMaster();
						}
					} else if (tile != caller) { // if the other doesn't have a master, and isn't the one to ask us to check re-evaluate
						updateCandidates.add(tc);
					}
				} else if (tile instanceof TileTFTMachineController) {
					if (hasMaster() && !isMaster(tile)) {
						//if we have a master, but it's not the same as this one then have no master because it's ambiguous
						removeMaster();
					} else {
						setMasterCoords(tile.xCoord, tile.yCoord, tile.zCoord);
					}

				}
			}
		}

		for (TileMachineComponent tc : updateCandidates) {
			//tc.CheckForMaster(this);
		}
	}

	private void removeMaster() {
		getMaster().removeComponent(this);
		masterY = -1;

	}

	private TileTFTMachineController getMaster() {
		return (TileTFTMachineController) worldObj.getTileEntity(masterX,
				masterY,
				masterZ);
	}

	private boolean isMaster(TileEntity tile) {

		return hasMaster() && masterX == tile.xCoord && masterY == tile.yCoord
				&& masterZ == tile.zCoord;
	}

	public boolean hasSameMaster(TileMachineComponent tc) {

		return tc.getMasterX() == getMasterX()
				&& tc.getMasterY() == getMasterY()
				&& tc.getMasterZ() == getMasterZ();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		//if (this instanceof TileMachineHeatingElement) {
		//	conductivity = 0.5f;
		//} else {
		//	conductivity = 0.01f;
		//}
		if (worldObj.isRemote) {
			return;
		}

		for (ForgeDirection dir : ForgeDirection.values()) {
			if (dir == ForgeDirection.UNKNOWN) {
				continue;
			}
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			if (tile == null || !(tile instanceof TileMachineComponent)) {
				float ambientTemp;
				Block b = worldObj.getBlock(x, y, z);
				if (b instanceof IFluidBlock) {
					Fluid f = ((IFluidBlock) b).getFluid();
					ambientTemp = f.getTemperature();
				} else {
					ambientTemp = TFC_Climate.getHeightAdjustedTemp(worldObj,
							x,
							y,
							z);
				}
				float ambientConductivity = AIR_CONDUCTIVITY;
				//if (!b.isAir(worldObj, x, y, z)) { //TODO: different blocks with different conductivity levels?
				//ambientConductivity /= 2;
				//}

				temperature -= 1 / 6f * (temperature - ambientTemp) * 0.5f
						* (getConductivity() + ambientConductivity);
			} else {
				TileMachineComponent tc = (TileMachineComponent) tile;
				float dTemp = 1 / 6f * (temperature - tc.getTemperature())
						* 0.5f * (getConductivity() + tc.getConductivity());
				temperature -= dTemp;
				tc.temperature += dTemp;

			}
		}
		this.sendServerToClientMessage();
	}

	private float getConductivity() {
		return conductivity;
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		//data.setInteger("masterX", masterX);
		//data.setInteger("masterY", masterY);
		//data.setInteger("masterZ", masterZ);

		data.setFloat("Temperature", temperature);
		data.setFloat("Conductivity", conductivity);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		//masterX = data.getInteger("masterX");
		//masterY = data.getInteger("masterY");
		//masterZ = data.getInteger("masterZ");

		temperature = data.getFloat("Temperature");
		conductivity = data.getFloat("Conductivity");
	}

	public boolean hasMaster() {
		return worldObj.getTileEntity(masterX, masterY, masterZ) instanceof TileTFTMachineController;
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
		getMaster().addComponent(this);
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		//if(worldObj.isRemote){
		this.readPacketNBT(packet.func_148857_g());
		//}
	}

	protected void readPacketNBT(NBTTagCompound nbt) {
		//masterX = nbt.getInteger("masterX");
		//masterY = nbt.getInteger("masterY");
		//masterZ = nbt.getInteger("masterZ");
		temperature = nbt.getFloat("temperature");

	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writePacketNBT(nbt);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 3, nbt);

	}

	protected void writePacketNBT(NBTTagCompound nbt) {
		//nbt.setInteger("masterX", masterX);
		//nbt.setInteger("masterY", masterY);
		//nbt.setInteger("masterZ", masterZ);
		nbt.setFloat("temperature", temperature);

	}

	public boolean openGui(World world, EntityPlayer player) {
		//if (!hasMaster()) {
		//	return false;
		//}
		player.openGui(TFTechness2.instance,
				TFTGuis.Machine.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		//getMasterX(),
		//getMasterY(),
		//getMasterZ());
		return true;

	}

	@Override
	public void setTargetTemperature(int max) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getTargetTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

	public void sendClientToServerMessage(NBTTagCompound nbt) {
		TFTechness2.snw.sendToServer(new PacketMachineComponent(this,
				Side.CLIENT));

	}

	private void sendServerToClientMessage() {
		this.markDirty();
		TFTechness2.snw.sendToAllAround(new PacketMachineComponent(this,
				Side.SERVER), new TargetPoint(worldObj.provider.dimensionId,
				xCoord, yCoord, zCoord, 64));

	}

	public void writeServerToClientMessage(NBTTagCompound nbt) {
		nbt.setFloat("temperature", temperature);

	}

	public void writeClientToServerMessage(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	public void readServerToClientMessage(NBTTagCompound nbt) {
		temperature = nbt.getFloat("temperature");

	}

	public void readClientToServerMessage(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	// light level emitted
	public int getLightLevel() {
		return (int) Math.min(15, Math.max(0, (temperature - 480) / 120));
	}
}
