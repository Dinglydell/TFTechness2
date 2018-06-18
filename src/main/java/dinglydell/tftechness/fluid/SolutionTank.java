package dinglydell.tftechness.fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemMeta;
import dinglydell.tftechness.item.TFTPropertyRegistry;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.recipe.SolutionRecipe;
import dinglydell.tftechness.recipe.SolutionRecipe.Condition;
import dinglydell.tftechness.recipe.TFTAlloyRecipe;

public class SolutionTank {
	//very simplified since air has a varying SH
	protected static final int SH_AIR = 1376;
	//also simplified
	protected static final float DENSITY_AIR = 1.2f;
	/**
	 * The amount (mB) of fluid that will melt/solidify per degree in
	 * temperature difference between the ambient temperature and the melting
	 * point
	 */
	private static final float MELT_CONSTANT = 10f;
	protected Map<Fluid, FluidStack> fluids;
	/** stored by mass kg */
	protected Map<ItemMeta, Float> solids;
	/** stored by moles */
	protected Map<ItemMeta, Float> solutes;

	protected Map<Gas, GasStack> gases;

	protected final int capacity;
	private Set<Condition> conditions = new HashSet<Condition>();

	public SolutionTank(int capacity) {
		fluids = new HashMap<Fluid, FluidStack>();
		solids = new HashMap<ItemMeta, Float>();
		solutes = new HashMap<ItemMeta, Float>();
		gases = new HashMap<Gas, GasStack>();
		gases.put(Gas.AIR, new GasStack(Gas.AIR, 1));
		this.capacity = capacity;
	}

	public void addCondition(Condition condition) {
		this.conditions.add(condition);
	}

	public void removeCondition(Condition condition) {
		this.conditions.remove(condition);
	}

	public Collection<FluidStack> getFluids() {

		return new ArrayList<FluidStack>(fluids.values());
	}

	/** The total volume (mB) that fluids take up in the tank. */
	public int getFluidAmount() {
		int amt = 0;
		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			amt += fluid.getValue().amount;
		}
		return amt;
	}

	/** The total volume (mB) that solids take up in the tank. */
	public int getSolidAmount() {
		int amt = 0;
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {
			amt += getSolidVolume(solid.getKey());
		}
		return amt;
	}

	/** The total volume (mB) that solutes take up in the tank. */
	public int getSoluteAmount() {
		int amt = 0;
		for (Entry<ItemMeta, Float> solute : solutes.entrySet()) {
			ItemStack is = solute.getKey().stack;
			// divide by mol/item, multiply by vol/item
			amt += 1000 * solute.getValue() / TFTPropertyRegistry.getMoles(is)
					* TFTPropertyRegistry.getVolumeDensity(is);
		}
		return amt;
	}

	/**
	 * Volume, in mB, of the ALL of contents of the tank (solids + fluids +
	 * solutes)
	 */
	public int getContentAmount() {
		return getFluidAmount() + getSolidAmount() + getSoluteAmount();

	}

	public int getCapacity() {
		return capacity;
	}

	public int getAir() {
		return capacity - getContentAmount();
	}

	public float getSHMass() {

		float SHMass = getAir() * 0.001f * SH_AIR * DENSITY_AIR;
		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			if (fluid.getKey() instanceof FluidMoltenMetal) {
				FluidMoltenMetal f = (FluidMoltenMetal) fluid.getKey();
				MetalStat stat = TFTechness2.statMap.get(f.metalName);
				SHMass += fluid.getValue().amount * 0.001f * stat.density
						* stat.getSISpecificHeat();
			}
		}
		for (Entry<ItemMeta, Float> solute : solutes.entrySet()) {

			ItemStack is = new ItemStack(solute.getKey().item, 1,
					solute.getKey().meta);
			HeatIndex hi = HeatRegistry.getInstance().findMatchingIndex(is);
			if (hi == null) {
				continue;
			}
			float mass = TFTPropertyRegistry.getDensity(is) * solute.getValue()
					/ (float) TFTPropertyRegistry.getMoles(is);
			SHMass += mass * hi.specificHeat * 1000;

		}
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {
			HeatIndex hi = HeatRegistry.getInstance()
					.findMatchingIndex(solid.getKey().stack);
			if (hi == null) {
				continue;
			}
			SHMass += hi.specificHeat * 1000 * solid.getValue();
		}

		return SHMass;
	}

	/** Updates the tank and returns the new temperature */
	public float updateTank(float temperature) {

		// fluids solidifying and stuff
		List<Fluid> fluidDelete = new ArrayList<Fluid>();
		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			if (fluid.getKey() instanceof FluidMoltenMetal) {
				FluidMoltenMetal f = (FluidMoltenMetal) fluid.getKey();
				f.setTemperature(temperature, fluid.getValue());
				ItemStack solid = TFTPropertyRegistry.getSolid(f);
				HeatIndex hi = HeatRegistry.getInstance()
						.findMatchingIndex(solid);
				if (temperature < hi.meltTemp) {
					float dT = hi.meltTemp - temperature;

					int amt = Math.min((int) (dT * MELT_CONSTANT),
							fluid.getValue().amount);
					fluid.getValue().amount -= amt;
					temperature += amt / MELT_CONSTANT;
					if (fluid.getValue().amount <= 0) {
						fluidDelete.add(f);
					}
					fill(solid,
							TFTPropertyRegistry.getDensity(solid)
									* (amt / 1000f)
									/ TFTPropertyRegistry
											.getVolumeDensity(solid),
							true);
				}
			}
		}

		for (Fluid f : fluidDelete) {
			fluids.remove(f);
		}

		List<ItemMeta> delete = new ArrayList<ItemMeta>();
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {
			//dissolve soluble solids
			float totalSol = getTotalSolubility(solid.getKey().stack);

			if (totalSol != 0) {
				if (!solutes.containsKey(solid.getKey())) {
					solutes.put(solid.getKey(), 0f);
				}
				//how much (kg) to reach capacity
				int molPerItem = TFTPropertyRegistry
						.getMoles(solid.getKey().stack);
				float kgPerItem = TFTPropertyRegistry
						.getDensity(solid.getKey().stack);
				float maxAmt = Math
						.min(solid.getValue(),
								kgPerItem
										* (totalSol - solutes.get(solid
												.getKey())) / molPerItem);

				if (maxAmt > 0) {
					//if not, dissolve it
					float massChange = maxAmt * 0.1f;
					float molChange = molPerItem * massChange / kgPerItem;
					solutes.put(solid.getKey(), solutes.get(solid.getKey())
							+ molChange);
					solid.setValue(solid.getValue() - massChange);
					if (solid.getValue() <= Float.MIN_VALUE * 10) {
						delete.add(solid.getKey());
						continue;
					}
				}

			}
			//melt solids
			HeatIndex hi = HeatRegistry.getInstance()
					.findMatchingIndex(solid.getKey().stack);
			if (hi != null && temperature > hi.meltTemp) {
				float dT = temperature - hi.meltTemp;
				float mass = solid.getValue();
				float dens = TFTPropertyRegistry
						.getDensity(solid.getKey().stack);
				float volDens = TFTPropertyRegistry.getVolumeDensity(solid
						.getKey().stack);
				int fluidEq = (int) (mass / dens * volDens * 1000);
				int fluidFill = Math.min(fluidEq, (int) (dT * MELT_CONSTANT));

				temperature -= fluidFill / MELT_CONSTANT;
				float dMass = fluidFill / (volDens * 1000) * dens;
				solid.setValue(solid.getValue() - dMass);
				if (solid.getValue() <= 0) {
					delete.add(solid.getKey());
				}

				FluidMoltenMetal molten = TFTPropertyRegistry.getMolten(solid
						.getKey().stack);
				fill(molten.createStack(fluidFill, temperature), true);
				continue;
			}
		}

		for (ItemMeta m : delete) {
			solids.remove(m);
		}

		//precipitation of solute
		Map<ItemMeta, Float> solutesCopy = new HashMap<ItemMeta, Float>(solutes);
		for (Entry<ItemMeta, Float> sol : solutesCopy.entrySet()) {
			float totalSol = getTotalSolubility(sol.getKey().stack);
			if (sol.getValue() > totalSol) {
				//precipitate
				//float newSol = totalSol;
				solutes.put(sol.getKey(), totalSol);
				float diff = sol.getValue() - totalSol;
				int molPerItem = TFTPropertyRegistry
						.getMoles(sol.getKey().stack);
				float kgPerItem = TFTPropertyRegistry
						.getDensity(sol.getKey().stack);
				float precipitateMass = kgPerItem * diff / molPerItem;

				fill(sol.getKey().stack, precipitateMass, true);

			}
			if (totalSol == 0) {
				solutes.remove(sol.getKey());
			}
		}

		//alloysh and shtuff
		//TODO: improve performance on this check
		for (TFTAlloyRecipe alloy : TFTAlloyRecipe.getAlloys()) {
			boolean match = true;
			for (FluidStack in : alloy.inputs) {
				if (drain(in, false).amount < in.amount) {
					match = false;
					break;
				}
			}
			if (match) {
				for (FluidStack in : alloy.inputs) {
					drain(in, true);
				}
				fill(alloy.output, true);
			}
		}
		//do a reaction
		//TODO: improve performance on this check
		SolutionRecipe recipe = SolutionRecipe.findRecipe(this);
		if (recipe == null) {
			return temperature;
		}
		float rate = 0;
		switch (recipe.inputState) {
		case gas:
			break;
		case liquid:
			FluidMoltenMetal fluid = TFTPropertyRegistry
					.getMolten(recipe.input);
			FluidStack fs = fluids.get(fluid);
			if (fs.amount >= recipe.inputQuantity) {
				rate = (fs.amount / recipe.inputQuantity) / 10;
				fs.amount -= recipe.inputQuantity * rate;

				if (fs.amount == 0) {
					fluids.remove(fluid);
				}

			}
		case solid:
			//TODO
			break;
		case solute:
			ItemMeta im = new ItemMeta(recipe.input);
			float amt = solutes.get(im);
			if (amt >= 0) {
				rate = (amt / recipe.inputQuantity) / 10;
				solutes.put(im, amt - recipe.inputQuantity * rate);
				if (solutes.get(im) <= 0) {
					solutes.remove(im);
				}

			}
			break;
		default:
			break;

		}
		if (rate != 0) {
			//HeatIndex hi = HeatRegistry.getInstance()
			//		.findMatchingIndex(recipe.output);
			//if(hi.meltTemp > temperature){
			//FluidMoltenMetal output = TFTItemPropertyRegistry
			//	.getMolten(recipe.output);
			fill(recipe.output, recipe.outputQuantity * rate, true);
			//}
		}
		return temperature;
	}

	/**
	 * returns the total amount of solute that can be dissolved in the fluids
	 * present
	 */
	private float getTotalSolubility(ItemStack solute) {
		float totalSol = 0;
		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			totalSol += TFTPropertyRegistry.getSolubilityIn(solute,
					fluid.getKey())
					* fluid.getValue().amount;

		}
		return totalSol;
	}

	/** Fill the tank with a powdered item */
	public int fill(ItemStack stack, boolean doFill) {
		float dens = TFTPropertyRegistry.getDensity(stack);
		int amt = (int) (fill(stack, stack.stackSize * dens, false) / dens);
		if (doFill) {
			float vol = stack.stackSize
					* TFTPropertyRegistry.getVolumeDensity(stack);
			reduceGas(vol);
			fill(stack, amt * dens, true);
		}
		return amt;
	}

	/** leaks a certain volume of gas (used when something is enters) */
	private void reduceGas(float vol) {
		float totalAmt = 0;
		for (Entry<Gas, GasStack> g : gases.entrySet()) {
			totalAmt += g.getValue().amount;
		}
		for (Entry<Gas, GasStack> g : gases.entrySet()) {
			float volReduce = vol / totalAmt * g.getValue().amount;
			float totalV = 0.001f * (getCapacity() - getContentAmount());
			// this ensures the pressure and temperature will be constant if volume decreases
			g.getValue().amount *= (totalV - volReduce) / totalV;
			//float startP = g.getValue().getPressure();

		}

	}

	/** Fill with a certain mass of an item */
	public float fill(ItemStack stack, float amt, boolean doFill) {
		//if (!TFTItemPropertyRegistry.isPowder(stack)) {
		//	return 0;
		//}
		int totalAmt = getContentAmount();
		float volDens = TFTPropertyRegistry.getVolumeDensity(stack);
		float dens = TFTPropertyRegistry.getDensity(stack);
		float toFill = Math.max(0,
				Math.min(amt, 0.001f * dens * (capacity - getContentAmount())
						/ volDens - amt));
		if (doFill) {
			ItemMeta im = new ItemMeta(stack);
			if (!solids.containsKey(im)) {
				solids.put(im, 0f);
			}
			solids.put(im, solids.get(im) + toFill);
		}
		return toFill;
	}

	/** Fill the tank with some fluid */
	public int fill(FluidStack stack, boolean doFill) {
		if (stack.amount == 0) {
			return 0;
		}
		int amt = getContentAmount();
		int toFill = Math.max(0, Math.min(stack.amount, capacity - amt));
		if (doFill) {
			if (!fluids.containsKey(stack.getFluid())) {
				FluidStack fs = stack.copy();
				fs.amount = 0;
				fluids.put(stack.getFluid(), fs);
			}
			fluids.get(stack.getFluid()).amount += toFill;
		}
		return toFill;
	}

	public FluidStack drain(FluidStack stack, boolean doDrain) {
		Fluid f = stack.getFluid();
		FluidStack drainStack = stack.copy();
		if (!fluids.containsKey(f)) {
			drainStack.amount = 0;
			return drainStack;
		}

		FluidStack currentStack = fluids.get(f);
		if (currentStack.amount < drainStack.amount) {
			drainStack.amount = currentStack.amount;
		}
		if (doDrain) {
			currentStack.amount -= drainStack.amount;
			if (currentStack.amount == 0) {
				fluids.remove(f);
			}
		}
		return drainStack;
	}

	public void addTooltip(List<String> infoList) {
		float totalV = getContentAmount();
		infoList.add("Total Volume - " + totalV + "mB");
		float fluidAmt = getFluidAmount(); // 1000f;

		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			infoList.add(fluid.getValue().getLocalizedName() + " (l) - "
					+ fluid.getValue().amount + "mB");
		}
		for (Entry<ItemMeta, Float> solute : solutes.entrySet()) {
			ItemStack is = new ItemStack(solute.getKey().item, 1,
					solute.getKey().meta);
			infoList.add(StatCollector.translateToLocal(is.getUnlocalizedName()
					+ ".name")
					+ " (solute) - "
					+ Math.round(1000 * solute.getValue() / fluidAmt)
					/ 1000f
					+ "mol/mB");
		}
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {
			infoList.add(StatCollector.translateToLocal(solid.getKey().stack
					.getUnlocalizedName() + ".name")
					+ " (s) - "
					+ Math.round(1000 * solid.getValue())
					/ 1000f
					+ "kg");
		}

		for (Entry<Gas, GasStack> gas : gases.entrySet()) {
			infoList.add(gas.getValue()
					.getDisplayString(0.001f * (getCapacity() - totalV)));
		}
	}

	//public FluidStack drain(int maxDrain, boolean doDrain) {
	//	return null;
	//}

	public void readFromNBT(NBTTagCompound tag) {
		fluids.clear();
		solids.clear();
		solutes.clear();

		NBTTagList fluidTags = tag.getTagList("Fluids",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < fluidTags.tagCount(); i++) {
			NBTTagCompound fluid = fluidTags.getCompoundTagAt(i);
			FluidStack fs = FluidStack.loadFluidStackFromNBT(fluid);
			if (fs != null) {
				fluids.put(fs.getFluid(), fs);
			}
		}

		NBTTagList solidTags = tag.getTagList("Solids",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < solidTags.tagCount(); i++) {
			NBTTagCompound solid = solidTags.getCompoundTagAt(i);
			ItemMeta im = new ItemMeta(Item.getItemById(solid.getShort("ID")),
					solid.getShort("meta"));
			solids.put(im, solid.getFloat("amt"));

		}
		NBTTagList soluteTags = tag.getTagList("Solutes",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < soluteTags.tagCount(); i++) {
			NBTTagCompound solute = soluteTags.getCompoundTagAt(i);
			ItemMeta im = new ItemMeta(Item.getItemById(solute.getShort("ID")),
					solute.getShort("meta"));
			solutes.put(im, solute.getFloat("amt"));

		}
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList fluidTags = new NBTTagList();
		for (FluidStack fs : fluids.values()) {
			fluidTags.appendTag(fs.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("Fluids", fluidTags);
		NBTTagList itemTags = new NBTTagList();
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {

			itemTags.appendTag(getTag(solid));
		}
		tag.setTag("Solids", itemTags);
		NBTTagList soluteTags = new NBTTagList();
		for (Entry<ItemMeta, Float> solute : solutes.entrySet()) {
			soluteTags.appendTag(getTag(solute));

		}
		tag.setTag("Solutes", soluteTags);
		return tag;
	}

	private NBTTagCompound getTag(Entry<ItemMeta, Float> entry) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setShort("ID", (short) Item.getIdFromItem(entry.getKey().item));
		tag.setShort("meta", (short) entry.getKey().meta);
		tag.setFloat("amt", entry.getValue());
		return tag;

	}

	public float getSolid(Item item) {
		return getSolid(new ItemMeta(item, 0));
	}

	public float getSolid(ItemMeta itemMeta) {
		if (solids.containsKey(itemMeta)) {
			return solids.get(itemMeta);
		}
		return 0;
	}

	public float getSolidVolume(ItemMeta item) {
		ItemStack is = item.stack;
		return 1000 * solids.get(item) / TFTPropertyRegistry.getDensity(is)
				* TFTPropertyRegistry.getVolumeDensity(is);

	}

	public float getSolute(Item item) {
		return getSolute(new ItemMeta(item, 0));
	}

	public float getSolute(ItemMeta itemMeta) {
		if (solutes.containsKey(itemMeta)) {
			return solutes.get(itemMeta);
		}
		return 0;
	}

	public boolean contaisFluid(Fluid fluid) {
		return fluids.containsKey(fluid);
	}

	public boolean containsSolid(ItemStack stack) {

		return solids.containsKey(new ItemMeta(stack));
	}

	public boolean containsSolute(ItemStack stack) {

		return solutes.containsKey(new ItemMeta(stack));
	}

	public boolean hasCondition(Condition condition) {
		return this.conditions.contains(condition);
	}

	public int getFluidAmount(Fluid fluid) {
		if (fluids.containsKey(fluid)) {
			return fluids.get(fluid).amount;
		}
		return 0;
	}

	/**
	 * transfer some (10%) of our content into a neighbour tank
	 * 
	 * @param dir
	 *            the direction the neighbour tank is in
	 */
	public void equalise(SolutionTank tank, ForgeDirection dir) {
		//transfer 10% of what we have into our neighbour
		HashMap<Fluid, FluidStack> fluidsCopy = new HashMap<Fluid, FluidStack>(
				fluids);
		int drainAllocation = getFluidAmount();
		if (dir != ForgeDirection.DOWN) {
			drainAllocation /= 10;
			if (dir == ForgeDirection.UP) {
				drainAllocation /= 100;
			}
		}
		List<Entry<Fluid, FluidStack>> drainOrder = new ArrayList<Entry<Fluid, FluidStack>>(
				fluids.entrySet());
		if (dir.offsetY != 0) {
			final int offsetY = dir.offsetY;
			Comparator<Entry<Fluid, FluidStack>> comparator = new Comparator<Entry<Fluid, FluidStack>>() {
				@Override
				public int compare(Entry<Fluid, FluidStack> left,
						Entry<Fluid, FluidStack> right) {
					return (int) (offsetY
							* TFTPropertyRegistry.getDensity(left.getKey()) - offsetY
							* TFTPropertyRegistry.getDensity(right.getKey()));
				}
			};

			Collections.sort(drainOrder, comparator);
		}

		for (Entry<Fluid, FluidStack> f : drainOrder) {
			FluidStack drain = f.getValue().copy();
			drain.amount = Math.min(drain.amount, drainAllocation);
			drainAllocation -= drain.amount;
			drain = drain(drain, true);
			drain.amount -= tank.fill(drain, true);
			drainAllocation += drain.amount;
			fill(drain, true);

		}

		//and the solutes
		HashMap<ItemMeta, Float> solutesCopy = new HashMap<ItemMeta, Float>(
				solutes);
		for (Entry<ItemMeta, Float> solute : solutesCopy.entrySet()) {
			float otherAmt = 0;
			if (tank.solutes.containsKey(solute.getKey())) {
				otherAmt = tank.solutes.get(solute.getKey());
			}
			float loss = solute.getValue() / 10f;
			otherAmt += loss;
			tank.solutes.put(solute.getKey(), otherAmt);
			solutes.put(solute.getKey(), solute.getValue() - loss);
		}
	}

	/**
	 * Sorts fluids by density.
	 * 
	 * @param direction
	 *            +ve for smallest to largest, -ve for largest to smallest
	 */
	public List<FluidStack> getDensitySortedFluids(int direction) {
		List<FluidStack> drainOrder = new ArrayList<FluidStack>(fluids.values());
		final int dir = direction;
		Comparator<FluidStack> comparator = new Comparator<FluidStack>() {
			@Override
			public int compare(FluidStack left, FluidStack right) {
				return (int) (dir
						* TFTPropertyRegistry.getDensity(left.getFluid()) - dir
						* TFTPropertyRegistry.getDensity(right.getFluid()));
			}
		};

		Collections.sort(drainOrder, comparator);

		return drainOrder;

	}

	/** Whether the tank has anything in liquid form of any kind */
	public boolean containsAnyFluid() {
		return !fluids.isEmpty();
	}

	public Set<ItemMeta> getSolids() {
		return solids.keySet();

	}

	public Set<ItemMeta> getSolutes() {
		return solutes.keySet();

	}
}
