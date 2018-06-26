package dinglydell.tftechness.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.item.TFTPropertyRegistry;

public class SolutionRecipe {
	public enum EnumState {
		solid, liquid, gas, solute
	}

	public static final Condition electrodes = new Condition();
	private static final List<SolutionRecipe> recipes = new ArrayList<SolutionRecipe>();
	public final ItemStack output;
	public final Condition condition;
	public final EnumState inputState;
	public final ItemStack input;
	/** kg */
	public final float outputQuantity;
	public final int inputQuantity;

	public SolutionRecipe(ItemStack output, float outputQuantity,
			EnumState inputState, ItemStack input, int inputQuantity,
			Condition condition) {
		this.output = output;
		this.outputQuantity = outputQuantity;
		this.input = input;
		this.inputQuantity = inputQuantity;
		this.inputState = inputState;
		this.condition = condition;
	}

	public static void addRecipe(SolutionRecipe recipe) {
		recipes.add(recipe);
	}

	public static SolutionRecipe findRecipe(SolutionTank tank) {
		for (SolutionRecipe recipe : recipes) {
			if (recipe.matches(tank)) {
				return recipe;
			}
		}
		return null;
	}

	private boolean matches(SolutionTank tank) {
		if (!tank.hasCondition(condition)) {
			return false;
		}
		switch (inputState) {
		case gas:
			return false;
		case liquid:
			return tank.contaisFluid(TFTPropertyRegistry.getMolten(input));
		case solid:
			return tank.containsSolid(input);
		case solute:
			return tank.containsSolute(input);

		}
		return false;

	}

	public static class Condition {

	}

}
