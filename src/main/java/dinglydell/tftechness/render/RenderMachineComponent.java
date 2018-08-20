package dinglydell.tftechness.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import dinglydell.tftechness.block.component.ComponentMaterial;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class RenderMachineComponent implements ISimpleBlockRenderingHandler {

	public int renderComponent;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if (modelId == renderComponent) {
			TileMachineComponent tile = (TileMachineComponent) world
					.getTileEntity(x, y, z);
			ComponentMaterial m = tile.getBaseMaterial();

			//renderer.renderFa

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
		return renderComponent;
	}

}
