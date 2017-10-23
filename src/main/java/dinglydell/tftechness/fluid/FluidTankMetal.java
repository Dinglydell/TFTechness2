package dinglydell.tftechness.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/** Helper class to handle a tank full of molten metal */
public class FluidTankMetal implements IFluidTank {
	protected FluidStack contents;
	protected final int capacity;
	protected FluidMoltenMetal lockedTo;

	public FluidTankMetal(int capacity) {
		this.capacity = capacity;
	}

	public FluidTankMetal(int capacity, FluidMoltenMetal lockedTo) {
		this(capacity);
		this.lockedTo = lockedTo;
	}

	/**
	 * Fills the tank with molten metal - new temperature will be average of old
	 * temperatures (weighted by quantity).
	 */
	@Override
	public int fill(FluidStack stack, boolean doFill) {
		if (!(stack.getFluid() instanceof FluidMoltenMetal) || lockedTo != null
				&& lockedTo != stack.getFluid()) {
			return 0;
		}
		if (contents == null) {

			FluidStack newStack = stack.copy();
			if (newStack.amount > capacity) {
				newStack.amount = capacity;
			}
			if (doFill) {
				contents = newStack;
			}
			return newStack.amount;
		}
		if (stack.getFluid() != contents.getFluid()) {
			return 0;
		}

		FluidMoltenMetal fluid = (FluidMoltenMetal) stack.getFluid();
		FluidStack newStack = contents.copy();
		int newAmt = Math.min(capacity, newStack.amount + stack.amount);
		int gain = newAmt - newStack.amount;
		fluid.setTemperature(fluid.getTemperature(newStack)
				* (newStack.amount / (float) newAmt)
				+ fluid.getTemperature(stack) * (gain / (float) newAmt),
				newStack);
		newStack.amount = newAmt;

		if (doFill) {
			contents = newStack;
		}
		return gain;
	}

	/**
	 * Drains a fluid stack from the tank. Returns the fluid stack that was (or
	 * would be) drained
	 */
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (contents == null || contents.amount == 0) {
			return null;
		}
		FluidMoltenMetal fluid = (FluidMoltenMetal) contents.getFluid();
		FluidStack drainStack = contents.copy();
		drainStack.amount = maxDrain;
		if (drainStack.amount > contents.amount) {
			drainStack.amount = contents.amount;
		}
		fluid.setTemperature(fluid.getTemperature(contents), drainStack);
		if (doDrain) {
			contents.amount -= drainStack.amount;
			if (contents.amount == 0) {
				contents = null;
			}
		}

		return drainStack;
	}

	public int getCapacity() {
		return capacity;
	}

	public boolean isLocked() {
		return lockedTo != null;
	}

	public FluidMoltenMetal getLockedFluid() {
		return lockedTo;
	}

	@Override
	public FluidStack getFluid() {
		return contents;
	}

	@Override
	public int getFluidAmount() {
		return contents == null ? 0 : contents.amount;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(contents, capacity);
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("lockedTo")) {
			lockedTo = (FluidMoltenMetal) FluidRegistry.getFluid(tag
					.getString("lockedTo"));
		}
		contents = FluidStack.loadFluidStackFromNBT(tag);
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		if (contents != null) {
			contents.writeToNBT(tag);
		}
		if (lockedTo != null) {
			tag.setString("lockedTo", lockedTo.getName());
		}
		return tag;
	}
}
