package dinglydell.tftechness.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import dinglydell.tftechness.item.ItemMeta;

public class Mixture {
	public FluidStack mixture;
	public List<FluidStack> fluids;
	public Map<ItemMeta, Float> solutes;

	public Mixture(FluidStack mix) {
		this.mixture = mix;
		fluids = new ArrayList<FluidStack>();
		solutes = new HashMap<ItemMeta, Float>();
	}

	public Mixture addFluid(FluidStack f) {
		fluids.add(f);
		return this;
	}

	public Mixture addSolute(ItemStack solute, float moles) {
		solutes.put(new ItemMeta(solute), moles);
		return this;
	}

}
