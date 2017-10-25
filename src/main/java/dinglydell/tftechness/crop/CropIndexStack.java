package dinglydell.tftechness.crop;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc.Food.CropIndex;
import com.bioxx.tfc.TileEntities.TECrop;

/** a CropIndex that has an itemstack as its output instead of an item */
public class CropIndexStack extends CropIndex {

	private ItemStack output1Stack;
	private ItemStack output2Stack;

	public CropIndexStack(int id, String name, int type, int growth,
			int stages, float minGTemp, float minATemp,
			float nutrientUsageMultiplier, Item seed, int[] nutriRestore) {
		super(id, name, type, growth, stages, minGTemp, minATemp,
				nutrientUsageMultiplier, seed, nutriRestore);

	}

	public CropIndexStack(int id, String name, int type, int growth,
			int stages, float minGTemp, float minATemp, Item seed) {
		super(id, name, type, growth, stages, minGTemp, minATemp, seed);
	}

	public CropIndexStack(int id, String name, int type, int growth,
			int stages, float minGTemp, float minATemp,
			float nutrientUsageMultiplier, Item seed) {
		super(id, name, type, growth, stages, minGTemp, minATemp,
				nutrientUsageMultiplier, seed);
	}

	public CropIndexStack setOutput1(ItemStack stack, float oAvg) {
		setOutput1(stack.getItem(), oAvg);
		output1Stack = stack;
		return this;
	}

	public CropIndexStack setOutput2(ItemStack stack, float oAvg) {
		setOutput2(stack.getItem(), oAvg);
		output2Stack = stack;
		return this;
	}

	@Override
	public ItemStack getOutput1(TECrop crop) {
		return super.getOutput1(crop) == null ? null : output1Stack;
	}

	@Override
	public ItemStack getOutput2(TECrop crop) {
		return super.getOutput2(crop) == null ? null : output2Stack;
	}

}
