package dinglydell.tftechness.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.Metal;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.fluid.Gas;
import dinglydell.tftechness.fluid.Mixture;

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

	private static final float DEFAULT_FLUID_CONDUCTIVITY = 0.001f;

	protected static Map<ItemMeta, Boolean> powders = new HashMap<ItemMeta, Boolean>();

	/** mol m^-3 */
	protected static Map<ItemMeta, HashMap<Fluid, Float>> solubilityMap = new HashMap<ItemMeta, HashMap<Fluid, Float>>();

	/** mol/item */
	protected static Map<ItemMeta, Float> molesMap = new HashMap<ItemMeta, Float>();

	/** kg/item */
	protected static Map<ItemMeta, Float> densityMap = new HashMap<ItemMeta, Float>();
	/** m^3/item */
	protected static Map<ItemMeta, Float> volumeMap = new HashMap<ItemMeta, Float>();

	private static Map<ItemMeta, Fluid> moltenMap = new HashMap<ItemMeta, Fluid>();
	private static Map<Fluid, Gas> fluidToGasMap = new HashMap<Fluid, Gas>();

	private static Map<Fluid, ItemStack> solidMap = new HashMap<Fluid, ItemStack>();
	private static Map<Gas, Fluid> gasToFluidMap = new HashMap<Gas, Fluid>();

	private static Map<Fluid, Float> fluidMolesMap = new HashMap<Fluid, Float>();

	///**
	// * The amount this item will contribute to the greenhouse factor when it is
	//	 *combusted
	// */
	//	protected static Map<ItemMeta, Float> carbonContent = new HashMap<ItemMeta, Float>();

	private static Map<Fluid, Float> fluidConductivity = new HashMap<Fluid, Float>();

	private static Map<Fluid, Float> fluidSH = new HashMap<Fluid, Float>();

	private static Map<ItemMeta, Float> meltPointMap = new HashMap<ItemMeta, Float>();

	private static Map<Fluid, Float> freezePointMap = new HashMap<Fluid, Float>();

	protected static Map<Fluid, Mixture> mixtureMap = new HashMap<Fluid, Mixture>();

	private static Map<ItemMeta, TFTFuel> fuels = new HashMap<ItemMeta, TFTFuel>();

	private static Set<Item> ignitionSources = new HashSet<Item>();

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
	public static void registerNumMoles(ItemStack stack, float moles) {
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
		registerMolten(stack, molten, molten.getMeltingPoint());
	}

	public static void registerMolten(ItemStack stack, Fluid molten,
			float meltingPoint) {
		ItemMeta im = new ItemMeta(stack);
		moltenMap.put(im, molten);
		meltPointMap.put(im, meltingPoint);
		freezePointMap.put(molten, meltingPoint);
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
	public static float getMoles(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (molesMap.containsKey(im)) {
			return molesMap.get(im);
		}
		return DEFAULT_MOLES;
	}

	public static Fluid getMolten(ItemStack stack) {
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

	public static float getMeltingPoint(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (meltPointMap.containsKey(im)) {
			return meltPointMap.get(im);
		}

		return Float.MAX_VALUE;
	}

	public static float getFreezingPoint(Fluid fluid) {
		if (freezePointMap.containsKey(fluid)) {
			return freezePointMap.get(fluid);
		}
		return TFTechness2.ABSOLUTE_ZERO - 1;
	}

	public static ItemStack getSolid(Fluid f) {
		if (solidMap.containsKey(f)) {
			return solidMap.get(f);
		}
		return null;

	}

	public static FluidMoltenMetal getMolten(Metal metal) {
		return (FluidMoltenMetal) getMolten(new ItemStack(metal.ingot));

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

	//	public static void registerCarbonContentByMass(ItemStack stack, float mass) {
	//
	//		registerCarbonContent(stack, mass * TFTWorldData.MASS_TO_PPM);
	//	}
	//
	//	/** increase in ppm in atmos as a result of this emission */
	//	public static void registerCarbonContent(ItemStack stack, float amt) {
	//		carbonContent.put(new ItemMeta(stack), amt);
	//	}

	public static void registerFuel(ItemStack stack, TFTFuel fuel) {
		fuels.put(new ItemMeta(stack), fuel);

	}

	public static float getCarbonContent(ItemStack stack) {
		TFTFuel fuel = getFuel(stack);
		return fuel == null ? 0 : fuel.getCarbonContentByPpm();
	}

	public static void registerConductivity(Fluid f, float value) {
		fluidConductivity.put(f, value);
	}

	public static float getConductivity(Fluid f) {
		if (fluidConductivity.containsKey(f)) {
			return fluidConductivity.get(f);
		}
		return DEFAULT_FLUID_CONDUCTIVITY;
	}

	public static float getSpecificHeat(Fluid f) {
		if (fluidSH.containsKey(f)) {
			return fluidSH.get(f);
		}
		return 0;
	}

	public static void registerSpecificHeat(Fluid f, float value) {
		fluidSH.put(f, value);
	}

	public static void registerMixture(Mixture m) {
		mixtureMap.put(m.mixture.getFluid(), m);
	}

	public static Mixture getMixture(Fluid f) {
		if (mixtureMap.containsKey(f)) {
			return mixtureMap.get(f);
		}
		return null;
	}

	public static boolean isCombustible(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (fuels.containsKey(im)) {
			return true;
		}
		return false;
	}

	public static TFTFuel getFuel(ItemStack stack) {
		ItemMeta im = new ItemMeta(stack);
		if (fuels.containsKey(im)) {
			return fuels.get(im);
		}
		return null;
	}

	public static boolean isIgnitionSource(ItemStack stack) {
		if (ignitionSources.contains(stack.getItem())) {
			return true;
		}
		return false;
	}

	public static void registerIgnitionSource(Item fireStarter) {
		ignitionSources.add(fireStarter);
	}
}
