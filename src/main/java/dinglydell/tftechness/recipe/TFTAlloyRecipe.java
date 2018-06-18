package dinglydell.tftechness.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;

public class TFTAlloyRecipe {

	public static List<TFTAlloyRecipe> alloys = new ArrayList<TFTAlloyRecipe>();

	public static void addRecipe(FluidStack output, List<FluidStack> inputs) {
		alloys.add(new TFTAlloyRecipe(output, inputs));

	}

	public static List<TFTAlloyRecipe> getAlloys() {
		return alloys;
	}

	public FluidStack output;
	public List<FluidStack> inputs;

	public TFTAlloyRecipe(FluidStack output, List<FluidStack> inputs) {
		this.output = output;
		this.inputs = inputs;

	}

}
