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
import dinglydell.tftechness.util.ItemUtil;

public class SolutionTank {
	//very simplified since air has a varying SH
	protected static final int SH_AIR = 1376;
	//also simplified
	protected static final float DENSITY_AIR = 1.2f;
	protected Map<Fluid, FluidStack> fluids;
	protected Map<ItemMeta, ItemStack> solids;
	protected Map<ItemMeta, Integer> solutes;
	protected final int capacity;
	private Condition condition;

	public SolutionTank(int capacity) {
		fluids = new HashMap<Fluid, FluidStack>();
		solids = new HashMap<ItemMeta, ItemStack>();
		solutes = new HashMap<ItemMeta, Integer>();
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
		for (Entry<ItemMeta, ItemStack> solid : solids.entrySet()) {
			amt += 1000
					* solid.getValue().stackSize
					* TFTItemPropertyRegistry
							.getVolumeDensity(solid.getValue());
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
		for (Entry<ItemMeta, Integer> solute : solutes.entrySet()) {

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
		for (Entry<ItemMeta, ItemStack> solid : solids.entrySet()) {
			HeatIndex hi = HeatRegistry.getInstance()
					.findMatchingIndex(solid.getValue());
			if (hi == null) {
				continue;
			}
			SHMass += hi.specificHeat * 1000 * solid.getValue().stackSize
					* TFTItemPropertyRegistry.getDensity(solid.getValue());
		}

		return SHMass;
	}

	public void updateTank(float temperature) {
		List<ItemMeta> delete = new ArrayList<ItemMeta>();
		for (Entry<ItemMeta, ItemStack> solid : solids.entrySet()) {
			//dissolve soluble solids
			float totalSol = 0;
			for (Entry<Fluid, FluidStack> fluid : fluids.entrySet()) {
				totalSol += TFTItemPropertyRegistry.getSolubilityIn(solid
						.getValue(), fluid.getKey())
						* fluid.getValue().amount;

			}
			if (totalSol != 0) {
				if (!solutes.containsKey(solid.getKey())) {
					solutes.put(solid.getKey(), 0);
				}
				//if another item dissolves, will it have reached its capacity
				int newAmt = solutes.get(solid.getKey())
						+ TFTItemPropertyRegistry.getMoles(solid.getValue());

				if (newAmt <= totalSol) {
					//if not, dissolve it
					solutes.put(solid.getKey(), newAmt);
					solid.getValue().stackSize--;
					if (solid.getValue().stackSize == 0) {
						delete.add(solid.getKey());
						continue;
					}
				}

			}
			//melt solids
			HeatIndex hi = HeatRegistry.getInstance()
					.findMatchingIndex(solid.getValue());
			if (hi != null && temperature > hi.meltTemp) {
				int stack = solid.getValue().stackSize;
				solid.getValue().stackSize = 0;
				delete.add(solid.getKey());
				FluidMoltenMetal molten = TFTItemPropertyRegistry
						.getMolten(solid.getValue());
				fill(molten.createStack((int) (TFTItemPropertyRegistry
						.getVolumeDensity(solid.getValue()) * 1000),
						temperature), true);
				continue;
			}
		}

		//TODO: precipitation of solutes & solidifying of liquids
		for (ItemMeta m : delete) {
			solids.remove(m);
		}

		//do a reaction
		//TODO: improve performance on this check
		SolutionRecipe recipe = SolutionRecipe.findRecipe(this);
		if (recipe == null) {
			return;
		}
		boolean hasEnough = false;
		switch (recipe.inputState) {
		case gas:
			break;
		case liquid:
			FluidMoltenMetal fluid = TFTItemPropertyRegistry
					.getMolten(recipe.input);
			FluidStack fs = fluids.get(fluid);
			if (fs.amount >= recipe.inputQuantity) {
				fs.amount -= recipe.inputQuantity;
				if (fs.amount == 0) {
					fluids.remove(fluid);
				}
				hasEnough = true;
			}
		case solid:
			//TODO
			break;
		case solute:
			ItemMeta im = new ItemMeta(recipe.input);
			int amt = solutes.get(im);
			if (amt >= recipe.inputQuantity) {
				solutes.put(im, amt - recipe.inputQuantity);
				if (solutes.get(im) == 0) {
					solutes.remove(im);
				}
				hasEnough = true;
			}
			break;
		default:
			break;

		}
		if (hasEnough) {
			//HeatIndex hi = HeatRegistry.getInstance()
			//		.findMatchingIndex(recipe.output);
			//if(hi.meltTemp > temperature){
			FluidMoltenMetal output = TFTItemPropertyRegistry
					.getMolten(recipe.output);
			fill(output.createStack(recipe.outputQuantity, temperature), true);
			//}
		}

	}

	/** Fill the tank with a powdered item */
	public int fill(ItemStack stack, boolean doFill) {
		if (!TFTItemPropertyRegistry.isPowder(stack)) {
			return 0;
		}
		int amt = getContentAmount();
		float volDens = TFTItemPropertyRegistry.getVolumeDensity(stack);
		int toFill = Math.min(stack.stackSize,
				(int) ((capacity - amt) / volDens));
		if (doFill) {
			ItemMeta im = new ItemMeta(stack);
			if (!solids.containsKey(im)) {
				solids.put(im, ItemUtil.clone(stack, 0));
			}
			solids.get(im).stackSize += toFill;
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
		for (Entry<ItemMeta, Integer> solute : solutes.entrySet()) {
			ItemStack is = new ItemStack(solute.getKey().item, 1,
					solute.getKey().meta);
			infoList.add(StatCollector.translateToLocal(is.getUnlocalizedName()
					+ ".name")
					+ " (solute) - " + solute.getValue() / fluidAmt + "mol/mB");
		}
		for (Entry<ItemMeta, ItemStack> solid : solids.entrySet()) {
			infoList.add(StatCollector.translateToLocal(solid.getValue()
					.getUnlocalizedName() + ".name")
					+ " (s) - "
					+ (solid.getValue().stackSize * TFTItemPropertyRegistry
							.getDensity(solid.getValue())) + "kg");
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
			ItemStack is = ItemStack.loadItemStackFromNBT(solid);
			if (is != null) {
				solids.put(new ItemMeta(is), is);
			}
		}
		NBTTagList soluteTags = tag.getTagList("Solutes",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < soluteTags.tagCount(); i++) {
			NBTTagCompound solute = soluteTags.getCompoundTagAt(i);
			ItemMeta im = new ItemMeta(Item.getItemById(solute.getShort("ID")),
					solute.getShort("meta"));
			solutes.put(im, solute.getInteger("amt"));

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
		for (ItemStack is : solids.values()) {
			itemTags.appendTag(is.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("Solids", itemTags);
		NBTTagList soluteTags = new NBTTagList();
		for (Entry<ItemMeta, Integer> solute : solutes.entrySet()) {
			NBTTagCompound soluteTag = new NBTTagCompound();
			soluteTag.setShort("ID",
					(short) Item.getIdFromItem(solute.getKey().item));
			soluteTag.setShort("meta", (short) solute.getKey().meta);
			soluteTag.setInteger("amt", solute.getValue());

			soluteTags.appendTag(soluteTag);

		}
		tag.setTag("Solutes", soluteTags);
		return tag;
	}

	public int getSolute(Item alumina) {
		return getSolute(new ItemMeta(alumina, 0));
	}

	public int getSolute(ItemMeta itemMeta) {
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
}
