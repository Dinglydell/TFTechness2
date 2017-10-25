package dinglydell.tftechness.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockTFT implements ISimpleBlockRenderingHandler {

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
			renderer.drawCrossedSquares(block.getIcon(world,
					x,
					y,
					z,
					world.getBlockMetadata(x, y, z)),
					x,
					y,
					z,
					1f);
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
