package dinglydell.tftechness.crop;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc.Food.CropIndex;
import com.bioxx.tfc.Food.CropManager;

import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.tftechness.item.ItemTFTSeed;
import dinglydell.tftechness.util.StringUtil;

/**
 * Crops registered with the TFT crop manager will be automatically given a crop
 * block, a seed item and be registered with the TFC manager. Set the seed item
 * of the CropIndex to null - it'll be auto-assigned by the manager. The crop
 * block will look for icons in plants/crops/[cropName][stage] (eg.
 * plants/crops/hemp0)
 */
public class TFTCropManager {

	public List<CropIndex> crops;
	protected static final TFTCropManager INSTANCE = new TFTCropManager();

	public TFTCropManager() {
		crops = new ArrayList<CropIndex>();
	}

	public void registerCrop(CropIndex crop) {
		String name = "seed" + StringUtil.capitaliseFirst(crop.cropName);
		crop.seedItem = new ItemTFTSeed(crop.cropId).setUnlocalizedName(name);

		GameRegistry.registerItem(crop.seedItem,
				crop.seedItem.getUnlocalizedName());

		OreDictionary.registerOre("seed" + name, crop.seedItem);

		CropManager.getInstance().addIndex(crop);
		crops.add(crop);

	}

	public static TFTCropManager getInstance() {
		return INSTANCE;
	}

}
