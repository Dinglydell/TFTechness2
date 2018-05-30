package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.recipe.SolutionRecipe;

public class TileMachineComponentTank extends TileMachineInventory implements
		IFluidHandler {
	protected SolutionTank tank = new SolutionTank(1000);
	private ItemStack stack;

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		data.setTag("Tank", tank.writeToNBT());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		tank.readFromNBT(data.getCompoundTag("Tank"));
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}

		if (stack != null) {
			stack.stackSize -= tank.fill(stack, true);
			if (stack.stackSize == 0) {
				stack = null;
			}
		}
		tank.updateTank(temperature);
		tank.removeCondition(SolutionRecipe.electrodes);
		for (ForgeDirection dir : ForgeDirection.values()) {
			if (dir == ForgeDirection.UNKNOWN) {
				continue;
			}
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(x, y, z);
			if (tile != null) {
				if (tile instanceof TileMachineComponentTank) {

					TileMachineComponentTank tileTank = (TileMachineComponentTank) tile;
					tank.equalise(tileTank.tank);
				} else if (tile instanceof TileMachineElectrode) {
					TileMachineElectrode tileElectrode = (TileMachineElectrode) tile;

					if (tileElectrode.getEnergyConsumptionRate() > 0
							&& tileElectrode.hasElectrodes()) {
						tank.addCondition(SolutionRecipe.electrodes);
					}
				}
			}
		}
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		super.writeServerToClientMessage(nbt);

		nbt.setTag("Tank", tank.writeToNBT());
	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {

		super.readServerToClientMessage(nbt);
		tank.readFromNBT(nbt.getCompoundTag("Tank"));
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return stack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack st = stack;
		stack = null;
		return st;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack st) {
		stack = st;

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
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {

		return true;
	}

	public SolutionTank getTank() {
		return tank;
	}

	public boolean openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Tank.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}
}
