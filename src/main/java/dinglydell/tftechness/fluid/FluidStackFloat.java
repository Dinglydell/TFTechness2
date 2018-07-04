package dinglydell.tftechness.fluid;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * A FluidStack that can have a float amount
 * 
 * also has an innate temperature
 */
public class FluidStackFloat {

	private Fluid fluid;
	public float amount;
	private float temperature;

	public FluidStackFloat(Fluid f, float amount, float temperature) {
		this.fluid = f;
		this.amount = amount;
		this.temperature = temperature;
	}

	public FluidStackFloat(FluidStack stack) {
		this(stack.getFluid(), stack.amount, stack.getFluid()
				.getTemperature(stack));
		//setTemperature();
	}

	public void setTemperature(float temp) {
		this.temperature = temp;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public Fluid getFluid() {
		return fluid;
	}

	public FluidStackFloat copy() {

		return new FluidStackFloat(fluid, amount, temperature);
	}

	public String getLocalizedName() {

		return fluid.getLocalizedName(new FluidStack(fluid, (int) amount));
	}

	public static FluidStackFloat loadFluidStackFromNBT(NBTTagCompound nbt) {
		String fluidName = nbt.getString("FluidName");
		if (fluidName == null || FluidRegistry.getFluid(fluidName) == null) {
			return null;
		}
		FluidStackFloat stack = new FluidStackFloat(
				FluidRegistry.getFluid(fluidName), nbt.getFloat("Amount"),
				nbt.getFloat("Temperature"));
		return stack;
	}

	public NBTBase writeToNBT(NBTTagCompound nbt) {
		nbt.setString("FluidName", fluid.getName());
		nbt.setFloat("Amount", amount);
		nbt.setFloat("Temperature", temperature);
		return nbt;
	}

	/** Gets the FluidStack representation - amount rounded to the nearest int */
	public FluidStack getStack() {
		if (fluid instanceof FluidMoltenMetal) {
			return ((FluidMoltenMetal) fluid).createStack((int) amount,
					temperature);
		}
		return new FluidStack(fluid, (int) amount);
	}
}
