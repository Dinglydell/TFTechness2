package dinglydell.tftechness.recipe;

import java.util.ArrayList;
import java.util.List;

import dinglydell.tftechness.fluid.FluidStackFloat;

public class TFTAlloyRecipe {

	public static List<TFTAlloyRecipe> alloys = new ArrayList<TFTAlloyRecipe>();

	public static void addRecipe(FluidStackFloat output,
			List<FluidStackFloat> inputs) {
		alloys.add(new TFTAlloyRecipe(output, inputs));

	}

	public static List<TFTAlloyRecipe> getAlloys() {
		return alloys;
	}

	public FluidStackFloat output;
	public List<FluidStackFloat> inputs;

	public TFTAlloyRecipe(FluidStackFloat output, List<FluidStackFloat> inputs) {
		this.output = output.copy();
		this.output.amount = 1;
		this.inputs = new ArrayList<FluidStackFloat>();
		for (FluidStackFloat in : inputs) {
			FluidStackFloat newIn = in.copy();
			newIn.amount /= output.amount;
			this.inputs.add(newIn);
		}

	}

}
