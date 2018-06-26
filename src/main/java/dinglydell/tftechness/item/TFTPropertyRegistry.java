package dinglydell.tftechness.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.Gas;

/**
 * Properties that are assigned to certain items or fluids for various
 * processes. At the moment, this is only used for the electrolyser.
 * */
public class TFTPropertyRegistry {

	/** Density of an item with no entry */
	private static final float DEFAULT_DENSITY = 1;

	private static final float DEFAULT_DENSITY_HEAVY = 100;
	private static final float DEFAULT_DENSITY_MEDIUM = 10;
	private static final float DEFAULT_DENSITY_LIGHT = 1;

	private static final int DEFAULT_MOLES = 100;

	private static final float DEFAULT_VOLUME_HUGE = 10;
	private static final float DEFAULT_VOLUME_VERYLARGE = 5;
	private static final float DEFAULT_VOLUME_LARGE = 1;
	private static final float DEFAULT_VOLUME_MEDIUM = 0.1f;
	private static final float DEFAULT_VOLUME_SMALL = 0.01f;

	private static final float DEFAULT_VOLUME_VERYSMALL = 0.001f;

	private static final float DEFAULT_VOLUME_TINY = 0.0001f;

	private static final float DEFAULT_VOLUME = 0.1f;

	protected static Map<ItemMeta, Boolean> powders = new HashMap<ItemMeta, Boolean>();

	/** mol m^-3 */
	protected static Map<ItemMeta, HashMap<Fluid, Float>> solubilityMap = new HashMap<ItemMeta, HashMap<Fluid, Float>>();

	/** mol/item */
	protected static Map<ItemMeta, Integer> molesMap = new HashMap<ItemMeta, Integer>();

	/** kg/item */
	protected static Map<ItemMeta, Float> densityMap = new HashMap<ItemMeta, Float>();
	/** m^3/item */
	protected static Map<ItemMeta, Float> volumeMap = new HashMap<ItemMeta, Float>();

	private static Map<ItemMeta, FluidMoltenMetal> moltenMap = new HashMap<ItemMeta, FluidMoltenMetal>();
	private static Map<Fluid, Gas> fluidToGasMap = new HashMap<Fluid, Gas>();

	private static Map<FluidMoltenMetal, ItemStack> solidMap = new HashMap<FluidMoltenMetal, ItemStack>();
	private static Map<Gas, Fluid> gasToFluidMap = new HashMap<Gas, Fluid>();

	private static Map<Fluid, Float> fluidMolesMap = new HashMap<Fluid, Float>();

	public static void registerPowder(ItemStack stack) {
		powders.put(new ItemMeta(stack), true);
	}

	/**
	 * @param sol
	 *            max solubility in mol m^-3
	 * */
	public static void registerSolute(ItemStack stack, Fluid fluid, float sol) {
		ItemMeta im = new ItemMeta(stack);
		if (!solubilityMap.containsKey(im)) {
			solubilityMap.put(im, new HashMap<Fluid, Float>());
		}
		solubilityMap.get(im).put(fluid, sol);
	}

	/** kg/item */
	public static void registerDensity(ItemStack stack, float density) {
		densityMap.put(new ItemMeta(stack), density);
	}

	/** m^3/item */
	public static void registerVolume(ItemStack stack, float volume) {
		volumeMap.put(new ItemMeta(stack), volume);
	}

	/** mol/item */
	public static void registerNumMoles(ItemStack stack, int moles) {
		molesMap.put(new ItemMeta(stack), moles);
	}

	/** mol/mB */
	public static void registerNumMoles(Fluid fluid, float moles) {
		fluidMolesMap.put(fluid, moles);
	}

	public static void registerBoil(Fluid fluid, Gas gas) {
		fluidToGasMap.put(fluid, gas);
		gasToFluidMap.put(gas, fluid);
	}

	public static void registerMolten(ItemStack stack, FluidMoltenMetal molten) {
		moltenMap.put(new ItemMeta(stack), molten);
		if (!solidMap.containsKey(molten)) {
			solidMap.put(molten, stack);
		}
	}

	public static boolean isPowder(ItemStack stack) {
		return powders.containsKey(new ItemMeta(stack));
	}

	public static boolean isSolubleIn(ItemStack stack, Fluid fluid) {
		ItemMeta im = new ItemMeta(stack);
		return solubilityMap.containsKey(im)
				&& solubilityMap.get(im).containsKey(fluid);
	}

	/** mol m^-3 */
	public static float getSolubilityIn(ItemStack stack, Fluid fluid) {
		if (!isSolubleIn(stack, fluid)) {
			return 0;
		}
		ItemMeta im = new ItemMeta(stack);
		return solubilityMap.get(im).get(fluid);
	}

	/** kg/item */
	public static float getDensity(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (densityMap.containsKey(im)) {
			return densityMap.get(im);
		}
		// try TFC weight
		if (stack.getItem() instanceof ItemTerra) {
			ItemTerra it = (ItemTerra) stack.getItem();
			EnumWeight weight = it.getWeight(stack);
			switch (weight) {
			case HEAVY:
				return DEFAULT_DENSITY_HEAVY;
			case LIGHT:
				return DEFAULT_DENSITY_LIGHT;

			case MEDIUM:
				return DEFAULT_DENSITY_MEDIUM;

			}
		}
		return DEFAULT_DENSITY;

	}

	/** kg/m^3 */
	public static float getDensity(Fluid fluid) {
		ItemStack solid = getSolid(fluid);

		return getDensity(solid) / getVolumeDensity(solid);
	}

	/** m^3/item */
	public static float getVolumeDensity(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (volumeMap.containsKey(im)) {
			return volumeMap.get(im);

		}
		// try TFC weight
		if (stack.getItem() instanceof ItemTerra) {
			ItemTerra it = (ItemTerra) stack.getItem();
			EnumSize size = it.getSize(stack);
			switch (size) {
			case HUGE:
				return DEFAULT_VOLUME_HUGE;

			case MEDIUM:
				return DEFAULT_VOLUME_MEDIUM;
			case LARGE:
				return DEFAULT_VOLUME_LARGE;
			case SMALL:
				return DEFAULT_VOLUME_SMALL;
			case TINY:
				return DEFAULT_VOLUME_TINY;
			case VERYLARGE:
				return DEFAULT_VOLUME_VERYLARGE;
			case VERYSMALL:
				return DEFAULT_VOLUME_VERYSMALL;

			}
		}
		return DEFAULT_VOLUME;

	}

	/** mol/item */
	public static int getMoles(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (molesMap.containsKey(im)) {
			return molesMap.get(im);
		}
		return DEFAULT_MOLES;
	}

	public static FluidMoltenMetal getMolten(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (moltenMap.containsKey(im)) {
			return moltenMap.get(im);
		}
		HeatIndex hi = HeatRegistry.getInstance().findMatchingIndex(stack);
		if (hi == null) {
			return null;
		}
		ItemStack out = hi.getOutput(new Random());
		if (!out.isItemEqual(stack)) {
			return getMolten(out);
		}
		///if (moltenMap.containsKey(ingot)) {
		//	return moltenMap.get(ingot);
		//}
		return null;
	}

	public static ItemStack getSolid(Fluid f) {
		if (solidMap.containsKey(f)) {
			return solidMap.get(f);
		}
		return null;

	}

	public static FluidMoltenMetal getMolten(Metal metal) {
		return getMolten(new ItemStack(metal.ingot));

	}

	public static Gas getGaseous(Fluid fluid) {
		if (fluidToGasMap.containsKey(fluid)) {
			return fluidToGasMap.get(fluid);
		}
		return null;
	}

	public static Fluid getLiquid(Gas gas) {
		if (gasToFluidMap.containsKey(gas)) {
			return gasToFluidMap.get(gas);
		}
		return null;
	}

	/** mol/mB */
	public static float getMoles(Fluid f) {
		if (fluidMolesMap.containsKey(f)) {
			return fluidMolesMap.get(f);
		}
		return 1f;

	}

}
