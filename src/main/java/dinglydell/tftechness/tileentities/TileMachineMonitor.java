package dinglydell.tftechness.tileentities;

import java.util.ArrayList;
import java.util.List;

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
	private float targetTemperature;

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
	public void setTargetTemperature(float target) {
		this.targetTemperature = target;
		this.sendClientToServerMessage();
	}

	@Override
	public float getTargetTemperature() {
		return targetTemperature;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("TargetTemperature", targetTemperature);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		targetTemperature = nbt.getFloat("TargetTemperature");
	}

	@Override
	public void writePacketNBT(NBTTagCompound nbt) {
		super.writePacketNBT(nbt);
		nbt.setFloat("TargetTemperature", targetTemperature);
	}

	@Override
	public void readPacketNBT(NBTTagCompound nbt) {

		super.readPacketNBT(nbt);
		targetTemperature = nbt.getFloat("TargetTemperature");
	}

	@Override
	public void writeClientToServerMessage(NBTTagCompound nbt) {

		super.writeClientToServerMessage(nbt);
		nbt.setFloat("TargetTemperature", targetTemperature);
	}

	@Override
	public void readClientToServerMessage(NBTTagCompound nbt) {

		super.readClientToServerMessage(nbt);
		targetTemperature = nbt.getFloat("TargetTemperature");
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		List<TileMachineHeatingElement> heaters = new ArrayList<TileMachineHeatingElement>();
		float dE = 0;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);

			if (tile instanceof TileMachineComponent) {
				if (tile instanceof TileMachineHeatingElement
						&& ((TileMachineHeatingElement) tile).isEnabled()) {
					heaters.add((TileMachineHeatingElement) tile);

				} else {
					TileMachineComponent tc = (TileMachineComponent) tile;
					dE -= getComponentEnergyChange(targetTemperature,
							tc,
							tc.getTemperature());
				}
			} else {
				dE -= getEnvironmentalEnergyChange(tile,
						targetTemperature,
						x,
						y,
						z);
			}
		}
		dE = -dE / heaters.size();
		for (TileMachineHeatingElement heater : heaters) {
			//HEAT_FLOW_MODIFIER * (temp - otherTemp) * 0.5f
			//* (getConductivity() + tc.getConductivity());

			float otherTargetTemp = targetTemperature
					+ 2
					* dE
					/ (HEAT_FLOW_MODIFIER * (getConductivity() + heater
							.getConductivity()));

			heater.setTargetTemperature(otherTargetTemp);
		}
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
						.setTargetTemperature(Integer.MAX_VALUE);
			}
		}
	}

}
