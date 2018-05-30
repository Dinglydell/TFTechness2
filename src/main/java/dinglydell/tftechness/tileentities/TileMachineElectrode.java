package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import blusunrize.immersiveengineering.common.IEContent;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.util.ItemUtil;

public class TileMachineElectrode extends TileMachineHeatingElement implements
		IInventory {
	//TODO: consider the possibility of removing the regular heating element such that they require electrodes

	private ItemStack[] inventory;

	public TileMachineElectrode() {
		inventory = new ItemStack[2];
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
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack stack) {
		return stack.getItem() == IEContent.itemGraphiteElectrode;
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
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;

	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	protected int spendRF(int amt) {
		if (hasElectrodes()) {
			return super.spendRF(amt);
		}
		return 0;
	}

	public boolean hasElectrodes() {

		return inventory[0] != null && inventory[1] != null;
	}

	public boolean openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Electrode.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}

}
