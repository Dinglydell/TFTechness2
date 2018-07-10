package dinglydell.tftechness.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.block.component.BlockTFTComponent;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineMonitor;
import dinglydell.tftechness.tileentities.TileMachineRF;
import dinglydell.tftechness.util.StringUtil;

/** A button that is another machine component - used for monitors */
public class GuiButtonComponent extends GuiButtonTFT {

	protected ItemStack stack;
	public TileMachineComponent tile;
	private boolean highlighted;
	private TileMachineMonitor monitor;
	protected static RenderItem renderItems = new RenderItem();

	public GuiButtonComponent(ForgeDirection dir, int x, int y, int width,
			int height, TileMachineComponent tile, TileMachineMonitor monitor) {
		super(dir.ordinal(), x, y, width, height, "");
		this.tile = tile;
		if (tile != null) {
			stack = BlockTFTComponent.getTileAsItemStack(tile);
		}
		this.monitor = monitor;
		//tile.getBlockType().getSheet()
	}

	public void setTile(TileMachineComponent tile) {

		this.tile = tile;
		if (tile == null) {
			stack = null;
		} else {
			stack = BlockTFTComponent.getTileAsItemStack(tile);
		}
	}

	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		if (stack == null) {
			return;
		}

		renderItems.renderItemIntoGUI(mc.fontRenderer,
				mc.renderEngine,
				stack,
				xPosition,
				yPosition);
		if (highlighted) {
			//drawHorizontalLine(xPosition, yPosition + 5, xPosition + width, 0);
			drawRect(xPosition, yPosition, xPosition + width, yPosition
					+ height, 0x55333366);
		}
		if (monitor.isThrottleCause(tile)) {
			drawRect(xPosition, yPosition, xPosition + width, yPosition
					+ height, 0x55663333);
		}

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
				tile.getMaxTemperature(),
				monitor.getThermometerTier());
		if (tile instanceof TileMachineRF) {
			tooltip.add(((TileMachineRF) tile).isGenerator() ? "Generating "
					: "Using " + ((TileMachineRF) tile).getEnergyRate()
							+ "RF/t");
		}
		((BlockTFTComponent) tile.getBlockType()).component.addTooltip(tooltip,
				stack.getTagCompound());

	}

	public void setHighlighted(boolean b) {
		this.highlighted = b;

	}

}
