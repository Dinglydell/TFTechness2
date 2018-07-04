package dinglydell.tftechness.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import cofh.api.energy.IEnergyReceiver;

import com.bioxx.tfc.Core.TFC_Climate;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.BlockMoltenMetal;
import dinglydell.tftechness.block.component.Component;
import dinglydell.tftechness.block.component.ComponentMaterial;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertySet;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.gui.component.ITileTemperature;
import dinglydell.tftechness.item.TFTPropertyRegistry;
import dinglydell.tftechness.network.PacketMachineComponent;
import dinglydell.tftechness.util.ItemUtil;

public/* abstract */class TileMachineComponent extends TileEntity implements
		ITileTemperature, IEnergyReceiver {
	protected static final float AIR_CONDUCTIVITY = 0.001f;
	//protected static final float FLUID_CONDUCTIVITY = 0.01f;
	protected static final float HEAT_FLOW_MODIFIER = 0.5f;// / 6;
	private int masterX, masterY = -1, masterZ;
	protected float temperature;
	protected int rf;
	//default capacity is 1s of HV power
	protected int rfCapacity = 20 * 4096;
	//protected float conductivity = 0.5f;

	//private ItemStack[] inventory;
	protected Component component;
	protected int oldLightLevel;
	/** The materials our properties come from */
	protected Map<ComponentProperty, ComponentMaterial> materials = new HashMap<ComponentProperty, ComponentMaterial>();

	//protected SolutionTank internalTank;

	public TileMachineComponent() {
		for (Component c : Component.components) {
			if (c.getType() == this.getClass()) {
				component = c;
				break;
			}
		}
	}

	public TileMachineComponent setRFCapacity(int capacity) {
		rfCapacity = capacity;
		return this;
	}

	//public TileMachineComponent setConductivity(float conductivity) {
	//	this.conductivity = conductivity;
	//
	//	return this;
	//}

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
				float ambientConductivity = AIR_CONDUCTIVITY;
				if (tile instanceof TileMoltenMetal) {
					ambientTemp = ((TileMoltenMetal) tile).getTemperature();
					ambientConductivity = ((TileMoltenMetal) tile)
							.getConductivity();
				} else if (b instanceof IFluidBlock) {
					Fluid f = ((IFluidBlock) b).getFluid();
					ambientTemp = f.getTemperature() - 273;

					ambientConductivity = TFTPropertyRegistry
							.getConductivity(f);

				} else {
					ambientTemp = TFC_Climate.getHeightAdjustedTemp(worldObj,
							x,
							y,
							z);
				}

				//if (!b.isAir(worldObj, x, y, z)) { //TODO: different blocks with different conductivity levels?
				//ambientConductivity /= 2;
				//}
				float dE = HEAT_FLOW_MODIFIER * (temperature - ambientTemp)
						* 0.5f * (getConductivity() + ambientConductivity);
				temperature -= dE / getSpecificHeat();
				if (tile instanceof TileMoltenMetal) {
					TileMoltenMetal t = (TileMoltenMetal) tile;
					t.addThermalEnergy(dE);
				}
			} else {
				TileMachineComponent tc = (TileMachineComponent) tile;
				float dE = HEAT_FLOW_MODIFIER
						* (temperature - tc.getTemperature()) * 0.5f
						* (getConductivity() + tc.getConductivity());
				//TODO: probably want to change this
				float mass = 1;
				temperature -= dE / (mass * getSpecificHeat());
				tc.temperature += dE / (mass * tc.getSpecificHeat());
				//temperature = 0;
				//tc.temperature = 0;

				float avgrf = (rf / (float) rfCapacity + tc.rf
						/ (float) tc.rfCapacity) / 2;
				if (rf / (float) rfCapacity > avgrf) {
					int desiredRf = (int) (rfCapacity * avgrf);
					rf -= tc.receiveEnergy(dir.getOpposite(),
							(rf - desiredRf),
							false);
				}
			}
		}

		int light = this.getLightLevel();
		if (light != oldLightLevel) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			//TFTechness2.logger.info("light " + light + " (T:" + temperature
			//		+ ")");
			//worldObj.updateLightByType(EnumSkyBlock.Block,
			//	xCoord,
			//yCoord,
			//zCoord);
		}
		oldLightLevel = light;

		if (temperature > getMaxTemperature()) {
			TFTechness2.logger.info("Melt!");
			//TODO: more reliable way of getting base material
			ComponentMaterial base = materials
					.get(ComponentProperty.SPECIFIC_HEAT);
			ItemStack p = ItemUtil.getStack(base.material);
			Fluid molten = TFTPropertyRegistry.getMolten(p);
			if (molten == null) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			} else {
				float amt = component.getBaseMaterialAmount(base.material,
						base.shelfMaterial);
				Block b = molten.getBlock();
				int meta = Math.max(0, (int) (15 - 15 * amt));
				worldObj.setBlock(xCoord, yCoord, zCoord, b, meta, 3);
				if (b instanceof BlockMoltenMetal) {
					TileEntity t = worldObj.getTileEntity(xCoord,
							yCoord,
							zCoord);
					if (t instanceof TileMoltenMetal) {
						((TileMoltenMetal) t).setTemperature(temperature);
						TFTechness2.logger.info("!");
					}
				}
			}
			//worldObj.setBlock(xCoord, yCoord, zCoord, p_147465_4_, p_147465_5_, p_147465_6_)
		}

		this.sendServerToClientMessage();
	}

	public float getConductivity() {
		if (!materials.containsKey(ComponentProperty.CONDUCTIVITY)) {
			return 0.5f;
		}
		return (Float) materials.get(ComponentProperty.CONDUCTIVITY).validFor
				.get(ComponentProperty.CONDUCTIVITY);
	}

	public float getSpecificHeat() {
		if (!materials.containsKey(ComponentProperty.SPECIFIC_HEAT)) {
			return 1000f;
		}
		return (Float) materials.get(ComponentProperty.SPECIFIC_HEAT).validFor
				.get(ComponentProperty.SPECIFIC_HEAT);
	}

	public float getMaxTemperature() {
		if (!materials.containsKey(ComponentProperty.MAXIMUM_TEMPERATURE)) {
			return Float.MAX_VALUE;
		}
		return (Float) materials.get(ComponentProperty.MAXIMUM_TEMPERATURE).validFor
				.get(ComponentProperty.MAXIMUM_TEMPERATURE);
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		//data.setInteger("masterX", masterX);
		//data.setInteger("masterY", masterY);
		//data.setInteger("masterZ", masterZ);

		data.setFloat("Temperature", temperature);
		data.setInteger("rf", rf);
		NBTTagCompound propData = new NBTTagCompound();
		writeComponentPropertiesToNBT(propData);
		data.setTag("properties", propData);
	}

	/**
	 * Writes just the component's properties to the nbt file - without
	 * temperature or other tileentity data
	 */
	public void writeComponentPropertiesToNBT(NBTTagCompound data) {

		for (ComponentPropertySet propSet : component.propertySets) {
			for (ComponentProperty prop : propSet.properties) {
				if (!materials.containsKey(prop)) {
					continue;
				}
				data.setString(prop.name, materials.get(prop).name);
			}
		}

		//component.writePropertiesToNBT(this, data);/
	}

	private void readPropertiesFromNBT(NBTTagCompound data) {
		NBTTagCompound propData = data.getCompoundTag("properties");
		for (ComponentPropertySet propSet : component.propertySets) {
			for (ComponentProperty prop : propSet.properties) {
				setProperty(prop, propData);
			}
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		//masterX = data.getInteger("masterX");
		//masterY = data.getInteger("masterY");
		//masterZ = data.getInteger("masterZ");

		temperature = data.getFloat("Temperature");

		readPropertiesFromNBT(data);
		rf = data.getInteger("rf");
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
		rf = nbt.getInteger("rf");
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
		nbt.setInteger("rf", rf);

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

	public void sendClientToServerMessage() {
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
		nbt.setInteger("rf", rf);
		nbt.setInteger("rfCapacity", rfCapacity);

	}

	public void writeClientToServerMessage(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	public void readServerToClientMessage(NBTTagCompound nbt) {
		temperature = nbt.getFloat("temperature");
		rf = nbt.getInteger("rf");
		rfCapacity = nbt.getInteger("rfCapacity");
	}

	public void readClientToServerMessage(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	// light level emitted
	public int getLightLevel() {
		return (int) Math.min(15, Math.max(0, (temperature - 480) / 100));
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		return rf;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return rfCapacity;
	}

	@Override
	public int receiveEnergy(ForgeDirection direction, int amt,
			boolean simulated) {
		int oldRf = rf;
		int newRf;
		if (amt > getMaxEnergyStored(direction)) {//fixes overflow issue with creative capacitors
			newRf = getMaxEnergyStored(direction);
		} else {
			newRf = Math.min(rf + amt, getMaxEnergyStored(direction));
		}
		if (!simulated) {
			rf = newRf;
		}
		return newRf - oldRf;
	}

	protected void setPlacedSide(int side) {
		//this method is used by machines that aren't the same on each side

	}

	public IIcon getIcon(int side) {
		//if (this.blockMetadata == -1) {
		//	return icons[0][0];
		//}

		return component.getIcon(0);
	}

	/** Called when the block is first placed into the world */
	public void initialiseComponent() {
		setPlacedSide(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		this.sendServerToClientMessage();

		this.temperature = TFC_Climate.getHeightAdjustedTemp(worldObj,
				xCoord,
				yCoord,
				zCoord);
	}

	public void setProperty(ComponentProperty prop, NBTTagCompound data) {
		if (!data.hasKey(prop.name)) {
			return;
		}
		materials.put(prop,
				ComponentMaterial.getMaterial(data.getString(prop.name)));

	}

	/**
	 * Stuff that happens when this block gets destroyed Used by the tank to
	 * cause explosions when pressure is high
	 */
	public void onDestroy() {

	}

}
