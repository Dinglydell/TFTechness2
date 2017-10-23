package dinglydell.tftechness.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidMoltenMetal extends Fluid {
	protected String metalName;

	public FluidMoltenMetal(String metalName) {
		super("molten" + metalName);
		this.metalName = metalName;
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
		return StatCollector.translateToLocal(getUnlocalizedName()) + " "
				+ StatCollector.translateToLocal("metal." + metalName);
	}

	@Override
	public int getTemperature(FluidStack stack) {
		return stack.tag.getInteger("Temperature");
	}

	public void setTemperature(int temperature, FluidStack stack) {

		NBTTagCompound nbt = stack.tag;
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		nbt.setFloat("Temperature", temperature);
		stack.tag = nbt;

	}

}
