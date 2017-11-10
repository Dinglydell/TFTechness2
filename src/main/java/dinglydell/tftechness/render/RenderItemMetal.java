package dinglydell.tftechness.render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemMetal;

/** Experimental class to render ingots */
public class RenderItemMetal implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return helper == ItemRendererHelper.ENTITY_BOBBING
				|| helper == ItemRendererHelper.ENTITY_ROTATION;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		ItemMetal m = (ItemMetal) item.getItem();
		int colour = (TFTechness2.materialMap.get(m.getMetal().name).colour);
		int r = (int) (colour / 65536);
		int g = ((colour / 256)) % 256;
		int b = colour % 256;
		r = 230;
		g = 150;
		b = 50;
		GL11.glColor3f(r / 255f, g / 255f, b / 255f);

		//GL11.glColor3b((byte) 255, (byte) 255, (byte) 255);
		IIcon icon = m.getIcon(item, 0);

		if (type.equals(ItemRenderType.INVENTORY)) {
			RenderItem.getInstance().renderIcon(0, 0, icon, 16, 16);
		} else {
			ItemRenderer.renderItemIn2D(Tessellator.instance,
					icon.getMaxU(),
					icon.getMinV(),
					icon.getMinU(),
					icon.getMaxV(),
					icon.getIconWidth(),
					icon.getIconHeight(),
					0.0625F);
			//GL11.glScalef(0.1f, 0.1f, 0.1f);
			//RenderItem.getInstance().renderIcon(0, 0, icon, 16, 16);
		}

		//RenderItem.getInstance().
	}
}
