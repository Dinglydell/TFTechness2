package dinglydell.tftechness.tileentities;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;

public class TileMachineMonitor extends TileMachineComponent {
	private ArrayList<TileMachineHeatingElement> heaters = new ArrayList<TileMachineHeatingElement>();
	protected int throttleCauseX = 0;
	protected int throttleCauseY = -1;
	protected int throttleCauseZ = 0;

	//private float targetTemperature;

	public boolean onRightClick(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Monitor.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}

	@Override
	public ThermometerTier getThermometerTier() {
		if (!materials.containsKey(ComponentProperty.THERMOMETER_TIER)) {
			return ThermometerTier.fuzzy;
		}
		return (ThermometerTier) materials
				.get(ComponentProperty.THERMOMETER_TIER).validFor
				.get(ComponentProperty.THERMOMETER_TIER);
	}

	@Override
	public void onNeighbourChange() {
		//heaters = new ArrayList<TileMachineHeatingElement>(); // find heaters
		heaters.clear();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);

			if (tile instanceof TileMachineHeatingElement) {
				heaters.add((TileMachineHeatingElement) tile);
			}
		}
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		super.writeServerToClientMessage(nbt);
		//TODO: write this in your own packet so this doesn't have to be sent constantly
		nbt.setInteger("throttleX", throttleCauseX);
		nbt.setInteger("throttleY", throttleCauseY);
		nbt.setInteger("throttleZ", throttleCauseZ);
	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {
		super.readServerToClientMessage(nbt);
		throttleCauseX = nbt.getInteger("throttleX");
		throttleCauseY = nbt.getInteger("throttleY");
		throttleCauseZ = nbt.getInteger("throttleZ");
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (worldObj.isRemote || heaters.size() == 0) {
			return;
		}
		setThrottleCause(this);
		float trueTarget = targetTemperature;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			if (tile instanceof TileMachineComponent) {
				TileMachineComponent tc = (TileMachineComponent) tile;
				float theirDE = -getEnergyChange(tc, tc.getTargetTemperature());

				float myTarget = tc.getTargetTemperature()
						+ 2
						* theirDE
						/ (HEAT_FLOW_MODIFIER * (getConductivity() + tc
								.getConductivity()));
				if (myTarget < trueTarget) {
					trueTarget = myTarget;
					setThrottleCause(tc);
				}

			}

		}
		float dE = getEnergyChange(this, trueTarget);
		dE = -dE / heaters.size();
		for (TileMachineHeatingElement heater : heaters) {
			//HEAT_FLOW_MODIFIER * (temp - otherTemp) * 0.5f
			//* (getConductivity() + tc.getConductivity());

			float otherTargetTemp = targetTemperature
					+ 2
					* dE
					/ (HEAT_FLOW_MODIFIER * (getConductivity() + heater
							.getConductivity()));
			if (heater.getTargetTemperature() < otherTargetTemp) {
				otherTargetTemp = heater.getTargetTemperature();
				setThrottleCause(heater);
			}
			heater.setThrottleTemperature(otherTargetTemp);
		}
	}

	private void setThrottleCause(TileMachineComponent tc) {
		throttleCauseX = tc.xCoord;
		throttleCauseY = tc.yCoord;
		throttleCauseZ = tc.zCoord;

	}

	private float getEnergyChange(TileMachineComponent other, float targetTemp) {
		float dE = 0;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);

			if (tile instanceof TileMachineComponent) {
				//if (tile instanceof TileMachineHeatingElement
				//	&& ((TileMachineHeatingElement) tile).isEnabled()) {
				//heaters.add((TileMachineHeatingElement) tile);

				//} else {
				if (tile != this
						|| (other == this && !(tile instanceof TileMachineHeatingElement))) {
					TileMachineComponent tc = (TileMachineComponent) tile;
					dE -= getComponentEnergyChange(targetTemp,
							tc,
							tc.getTemperature());
				}
			} else {
				dE -= getEnvironmentalEnergyChange(tile, targetTemp, x, y, z);
			}

		}
		return dE;
	}

	@Override
	public boolean onDestroy() { //clean up - dethrottle everything
		boolean destroy = super.onDestroy();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);

			if (tile instanceof TileMachineHeatingElement) {
				((TileMachineHeatingElement) tile)
						.setThrottleTemperature(Float.MAX_VALUE);
			}
		}
		return destroy;
	}

	public boolean isThrottleCause(TileMachineComponent tile) {
		return tile.xCoord == throttleCauseX && tile.yCoord == throttleCauseY
				&& tile.zCoord == throttleCauseZ;
	}

}
