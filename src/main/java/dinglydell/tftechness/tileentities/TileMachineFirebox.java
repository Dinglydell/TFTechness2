package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.item.TFTFuel;
import dinglydell.tftechness.item.TFTPropertyRegistry;
import dinglydell.tftechness.world.TFTWorldData;

public class TileMachineFirebox extends TileMachineInventory {

	public TileMachineFirebox() {
		super();
		inventory = new ItemStack[5];
	}

	protected TFTFuel fuel;
	protected int fuelTimer;
	protected boolean ignited;

	@Override
	public void updateEntity() {
		super.updateEntity();
		boolean shouldShift = false;
		for (int i = 0; i < inventory.length; i++) {
			if (shouldShift) {
				inventory[i - 1] = inventory[i];
			}
			if (inventory[i] == null) {
				shouldShift = true;
			}

		}
		if (shouldShift) {
			inventory[inventory.length - 1] = null;
		}

		//TODO: ignition temperature, probably different for each fuel

		if (fuel == null || fuelTimer >= fuel.burnTime) {
			TFTFuel f = TFTPropertyRegistry.getFuel(inventory[0]);
			if (f == null) {
				ignited = false;
				fuel = null;
			} else if (temperature >= f.ignitionTemperature) {
				ignited = true;
			}
			if (ignited) {
				fuel = f;
				fuelTimer = 0;
				inventory[0] = null;

			}
		} else if (ignited) {
			fuelTimer++;
			float dT = fuel.burnTemp - temperature;
			temperature += dT * 0.001f;
			//nothing to see here
			TFTWorldData.get(worldObj)
					.emitGreenhouse(fuel.getCarbonContentByPpm()
							/ fuel.burnTime);
		}

	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return TFTPropertyRegistry.isCombustible(stack);
	}

	@Override
	public boolean onRightClick(World world, EntityPlayer player) {

		ItemStack useItem = player.getHeldItem();
		if (useItem != null && TFTPropertyRegistry.isIgnitionSource(useItem)) {
			useItem.setItemDamage(useItem.getItemDamage() + 1);
			this.ignited = true;
			return true;
		}

		player.openGui(TFTechness2.instance,
				TFTGuis.Firebox.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}

	public boolean isIgnited() {
		return ignited;
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		super.writeServerToClientMessage(nbt);
		writeFireboxNBT(nbt);
	}

	private void writeFireboxNBT(NBTTagCompound nbt) {
		nbt.setBoolean("ignited", ignited);
		nbt.setInteger("fuelTimer", fuelTimer);
		if (fuel != null) {
			nbt.setString("fuelType", fuel.name);
		}

	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {
		super.readServerToClientMessage(nbt);
		ignited = nbt.getBoolean("ignited");
	}

	private void readFireboxNBT(NBTTagCompound nbt) {
		ignited = nbt.getBoolean("ignited");
		fuelTimer = nbt.getInteger("fuelTimer");
		fuel = TFTFuel.getFuel(nbt.getString("fuelType"));
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		writeFireboxNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		readFireboxNBT(data);
	}

	public int getFuelTimeLeft() {
		return fuel.burnTime - fuelTimer;
	}
}
