package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.block.component.BlockTFTComponent;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.util.StringUtil;

/** A button that is another machine component - used for monitors */
public class GuiButtonComponent extends GuiButtonTFT {

	protected ItemStack stack;
	public TileMachineComponent tile;
	protected static RenderItem renderItems = new RenderItem();

	public GuiButtonComponent(ForgeDirection dir, int x, int y, int width,
			int height, TileMachineComponent tile) {
		super(dir.ordinal(), x, y, width, height, "");
		this.tile = tile;
		if (tile != null) {
			stack = BlockTFTComponent.getTileAsItemStack(tile);
		}
		//tile.getBlockType().getSheet()
	}

	public void setTile(TileMachineComponent tile) {

		this.tile = tile;
		stack = BlockTFTComponent.getTileAsItemStack(tile);
	}

	@Override
	public void drawButton(Minecraft mc, int p_146112_2_, int p_146112_3_) {
		if (stack == null) {
			return;
		}
		renderItems.renderItemIntoGUI(mc.fontRenderer,
				mc.renderEngine,
				stack,
				xPosition,
				yPosition);

	}

	@Override
	public void addTooltip(List<String> tooltip) {
		//Minecraft mc = Minecraft.getMinecraft();
		//tooltip.addAll(stack.getTooltip(mc.thePlayer,
		//	mc.gameSettings.advancedItemTooltips));
		if (tile == null) {
			return;
		}
		tooltip.add(StatCollector.translateToLocal(stack.getUnlocalizedName()
				+ ".name"));
		StringUtil.addTemperatureTooltip(tooltip,
				tile.getTemperature(),
				tile.getMaxTemperature());
		((BlockTFTComponent) tile.getBlockType()).component.addTooltip(tooltip,
				stack.getTagCompound());

	}

}
