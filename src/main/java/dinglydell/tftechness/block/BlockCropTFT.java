package dinglydell.tftechness.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc.Blocks.BlockCrop;
import com.bioxx.tfc.Food.CropIndex;
import com.bioxx.tfc.Food.CropManager;
import com.bioxx.tfc.TileEntities.TECrop;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.crop.TFTCropManager;
import dinglydell.tftechness.render.RenderCropTFT;

public class BlockCropTFT extends BlockCrop {
	protected Map<CropIndex, IIcon[]> icons;
	protected IIcon placeholder;

	public BlockCropTFT() {
		super();

	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		super.registerBlockIcons(register);
		List<CropIndex> crops = TFTCropManager.getInstance().crops;
		icons = new HashMap<CropIndex, IIcon[]>();
		for (CropIndex crop : crops) {
			IIcon[] cropIcons = new IIcon[crop.numGrowthStages];
			for (int i = 0; i < crop.numGrowthStages; i++) {
				cropIcons[i] = register.registerIcon(TFTechness2.MODID
						+ ":plants/crops/" + crop.cropName + i);
				placeholder = cropIcons[i];
			}
			icons.put(crop, cropIcons);
		}
	}

	@Override
	public int getRenderType() {

		return RenderCropTFT.renderCrops;
	}

	// Please stop having hard-coded IDs, TFC
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int meta) {
		TECrop te = (TECrop) access.getTileEntity(x, y, z);
		//te.cropId = 19;
		CropIndex crop = CropManager.getInstance().getCropFromId(te.cropId);
		int stage = Math.min((int) te.growth, crop.numGrowthStages);
		if (!icons.containsKey(crop)) {
			return super.getIcon(access, x, y, z, meta);
		}
		return icons.get(crop)[Math.max(0, stage - 1)];
	}

}
