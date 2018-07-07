package dinglydell.tftechness.tileentities;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;

public class TileMachineMonitor extends TileMachineComponent {
	private ArrayList<TileMachineHeatingElement> heaters = new ArrayList<TileMachineHeatingElement>();

	//private float targetTemperature;

	public boolean openGui(World world, EntityPlayer player) {
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
	public void updateEntity() {

		super.updateEntity();
		if (worldObj.isRemote || heaters.size() == 0) {
			return;
		}

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

				trueTarget = Math.min(trueTarget, myTarget);

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

			heater.setThrottleTemperature(Math.min(otherTargetTemp,
					heater.getTargetTemperature()));
		}
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
	public void onDestroy() { //clean up - dethrottle everything
		super.onDestroy();
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
	}

}
