package dinglydell.tftechness.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import dinglydell.tftechness.TFTechness2;

public class FluidMoltenMetal extends Fluid {
	protected String metalName;
	protected float meltPoint;

	public FluidMoltenMetal(String metalName, float meltPoint) {
		super("molten" + metalName);
		this.metalName = metalName;
		this.meltPoint = meltPoint;
	}

	public String getMetalName() {
		return metalName;
	}

	public FluidStack createStack(int amount, float temperature) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("Temperature", temperature);
		return new FluidStack(this, amount, nbt);
	}

	@Override
	public String getLocalizedName(FluidStack stack) {
		return (getTemperature(stack) < meltPoint ? "" : StatCollector
				.translateToLocal("fluid.moltenMetal.name"))
				+ StatCollector
						.translateToLocal("metal." + metalName + ".name")
				+ " - " + getTemperature(stack) + TFTechness2.degrees + "C";
	}

	@Override
	public int getTemperature(FluidStack stack) {
		if (stack.tag == null) {
			//TODO: proper default
			setTemperature(1000, stack);
		}
		return stack.tag.getInteger("Temperature");
	}

	public void setTemperature(float temperature, FluidStack stack) {

		NBTTagCompound nbt = stack.tag;
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setFloat("Temperature", temperature);
		stack.tag = nbt;

	}

}
