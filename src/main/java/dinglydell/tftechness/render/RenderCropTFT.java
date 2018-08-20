package dinglydell.tftechness.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc.Food.CropManager;
import com.bioxx.tfc.Render.Blocks.RenderCrop;
import com.bioxx.tfc.TileEntities.TECrop;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import dinglydell.tftechness.crop.TFTCropManager;

public class RenderCropTFT implements ISimpleBlockRenderingHandler {

	public static int renderCrops;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		//if (modelId == renderCrops) {

		//}

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if (modelId == renderCrops) {
			TECrop tile = (TECrop) world.getTileEntity(x, y, z);
			// if is TFC crop, do TFC render
			if (!TFTCropManager.getInstance().crops.contains(CropManager
					.getInstance().getCropFromId(tile.cropId))) {
				return RenderCrop.render(block, x, y, z, renderer);
			}

			Tessellator tessellator = Tessellator.instance;
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world,
					x,
					y,
					z));
			int l = block.colorMultiplier(world, x, y, z);

			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;

			if (EntityRenderer.anaglyphEnable) {
				float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
				float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
				float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
				f = f3;
				f1 = f4;
				f2 = f5;
			}

			tessellator.setColorOpaque_F(f, f1, f2);
			renderer.drawCrossedSquares(block.getIcon(world,
					x,
					y,
					z,
					world.getBlockMetadata(x, y, z)),
					x,
					y,
					z,
					1f);
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
