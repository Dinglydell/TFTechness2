package dinglydell.tftechness.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.gui.component.ITileTemperature;
import dinglydell.tftechness.util.ItemUtil;

public class TileTFTMachineController extends TileEntity implements IInventory,
		ITileTemperature {

	List<TileMachineComponent> components = new ArrayList<TileMachineComponent>();
	//TODO: components with their separate inventories
	private ItemStack[] inventory = new ItemStack[0];
	/** The number of empty air blocks in the structure */
	int capacity = 0;

	protected float temperature;

	protected SolutionTank internalTank = new SolutionTank(capacity * 1000);

	public TileTFTMachineController() {

	}

	@Override
	public void updateEntity() {
		for (TileMachineComponent component : components) {
			//component.up
		}
		internalTank.updateTank(getTemperature());

	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	public float getTemperature() {
		return this.temperature;
		//return this.temperature / getNetSHMass() - 273;
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
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;

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
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
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

		return false;
	}

	public void addComponent(TileMachineComponent component) {
		components.add(component);
	}

	public void removeComponent(TileMachineComponent component) {
		components.remove(component);

	}

	public void openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Machine.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		//return true;

	}

	public void CheckForComponents() {
		for (ForgeDirection dir : ForgeDirection.values()) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX,
					yCoord + dir.offsetY,
					zCoord + dir.offsetZ);
			if (tile != null && tile instanceof TileMachineComponent) {
				((TileMachineComponent) tile).CheckForMaster();
			}
		}

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

}
