package dinglydell.tftechness.fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemMeta;
import dinglydell.tftechness.item.TFTItemPropertyRegistry;
import dinglydell.tftechness.metal.MetalStat;
import dinglydell.tftechness.recipe.SolutionRecipe;
import dinglydell.tftechness.recipe.SolutionRecipe.Condition;

public class SolutionTank {
	//very simplified since air has a varying SH
	protected static final int SH_AIR = 1376;
	//also simplified
	protected static final float DENSITY_AIR = 1.2f;
	protected Map<Fluid, FluidStack> fluids;
	/** stored by mass kg */
	protected Map<ItemMeta, Float> solids;
	/** stored by moles */
	protected Map<ItemMeta, Float> solutes;
	protected final int capacity;
	private Condition condition;

	public SolutionTank(int capacity) {
		fluids = new HashMap<Fluid, FluidStack>();
		solids = new HashMap<ItemMeta, Float>();
		solutes = new HashMap<ItemMeta, Float>();
		this.capacity = capacity;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Collection<FluidStack> getFluids() {

		return fluids.values();
	}

	public int getFluidAmount() {
		int amt = 0;
		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			amt += fluid.getValue().amount;
		}
		return amt;
	}

	public int getSolidAmount() {
		int amt = 0;
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {
			ItemStack is = new ItemStack(solid.getKey().item, 1,
					solid.getKey().meta);
			amt += 1000 * solid.getValue()
					/ TFTItemPropertyRegistry.getDensity(is)
					* TFTItemPropertyRegistry.getVolumeDensity(is);
		}
		return amt;
	}

	/** Volume, in mB, of the contents of the tank (solids + fluids) */
	public int getContentAmount() {
		return getFluidAmount() + getSolidAmount();

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
			float mass = TFTItemPropertyRegistry.getDensity(is)
					* solute.getValue()
					/ (float) TFTItemPropertyRegistry.getMoles(is);
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

	public void updateTank(float temperature) {
		List<ItemMeta> delete = new ArrayList<ItemMeta>();
		for (Entry<ItemMeta, Float> solid : solids.entrySet()) {
			//dissolve soluble solids
			float totalSol = 0;
			for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
				totalSol += TFTItemPropertyRegistry.getSolubilityIn(solid
						.getKey().stack, fluid.getKey())
						* fluid.getValue().amount;

			}
			if (totalSol != 0) {
				if (!solutes.containsKey(solid.getKey())) {
					solutes.put(solid.getKey(), 0f);
				}
				//how much (kg) to reach capacity
				int molPerItem = TFTItemPropertyRegistry.getMoles(solid
						.getKey().stack);
				float kgPerItem = TFTItemPropertyRegistry.getDensity(solid
						.getKey().stack);
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
				float mass = solid.getValue();
				solid.setValue(0f);
				delete.add(solid.getKey());
				FluidMoltenMetal molten = TFTItemPropertyRegistry
						.getMolten(solid.getKey().stack);
				fill(molten.createStack((int) (mass
						/ TFTItemPropertyRegistry.getDensity(solid.getKey().stack)
						* TFTItemPropertyRegistry.getVolumeDensity(solid
								.getKey().stack) * 1000),
						temperature),
						true);
				continue;
			}
		}

		//TODO: precipitation of solutes & solidifying of liquids
		for (ItemMeta m : delete) {
			solids.remove(m);
		}

		// fluids solidifying and stuff
		List<Fluid> fluidDelete = new ArrayList<Fluid>();
		for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
			if (fluid.getKey() instanceof FluidMoltenMetal) {
				FluidMoltenMetal f = (FluidMoltenMetal) fluid.getKey();
				f.setTemperature(temperature, fluid.getValue());
				ItemStack solid = TFTItemPropertyRegistry.getSolid(f);
				HeatIndex hi = HeatRegistry.getInstance()
						.findMatchingIndex(solid);
				if (temperature < hi.meltTemp) {
					int amt = fluid.getValue().amount;
					fluid.getValue().amount = 0;
					fluidDelete.add(f);
					fill(solid,
							TFTItemPropertyRegistry.getDensity(solid)
									* amt
									/ TFTItemPropertyRegistry
											.getVolumeDensity(solid),
							true);
				}
			}
		}
		for (Fluid f : fluidDelete) {
			fluids.remove(f);
		}

		//do a reaction
		//TODO: improve performance on this check
		SolutionRecipe recipe = SolutionRecipe.findRecipe(this);
		if (recipe == null) {
			return;
		}
		float rate = 0;
		switch (recipe.inputState) {
		case gas:
			break;
		case liquid:
			FluidMoltenMetal fluid = TFTItemPropertyRegistry
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
			if (amt >= recipe.inputQuantity) {
				rate = (amt / recipe.inputQuantity) / 10;
				solutes.put(im, amt - recipe.inputQuantity * rate);
				if (solutes.get(im) == 0) {
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
			FluidMoltenMetal output = TFTItemPropertyRegistry
					.getMolten(recipe.output);
			fill(output.createStack((int) (recipe.outputQuantity * rate),
					temperature), true);
			//}
		}

	}

	/** Fill the tank with a powdered item */
	public int fill(ItemStack stack, boolean doFill) {
		float dens = TFTItemPropertyRegistry.getDensity(stack);
		int amt = (int) (fill(stack, stack.stackSize * dens, false) / dens);
		if (doFill) {
			fill(stack, amt * dens, true);
		}
		return amt;
	}

	/** Fill with a certain mass of an item */
	public float fill(ItemStack stack, float amt, boolean doFill) {
		if (!TFTItemPropertyRegistry.isPowder(stack)) {
			return 0;
		}
		int totalAmt = getContentAmount();
		float volDens = TFTItemPropertyRegistry.getVolumeDensity(stack);
		float dens = TFTItemPropertyRegistry.getDensity(stack);
		float toFill = Math.min(amt, dens * capacity / volDens - amt);
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
		int amt = getContentAmount();
		int toFill = Math.min(stack.amount, capacity - amt);
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
		if (!fluids.containsKey(f)) {
			return null;
		}

		FluidStack drainStack = stack.copy();
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

		infoList.add("Total Volume - " + getContentAmount() + "mB");
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
		return condition == null || this.condition == condition;
	}

	public int getFluidAmount(Fluid fluid) {
		if (fluids.containsKey(fluid)) {
			return fluids.get(fluid).amount;
		}
		return 0;
	}
}
