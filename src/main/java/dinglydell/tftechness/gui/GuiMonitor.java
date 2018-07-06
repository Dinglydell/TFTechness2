package dinglydell.tftechness.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.component.GuiButtonComponent;
import dinglydell.tftechness.gui.component.GuiTemperature;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineMonitor;

public class GuiMonitor extends GuiMachine {
	public static final ResourceLocation MONITOR_TEXTURE = new ResourceLocation(
			TFTechness2.MODID + ":textures/gui/machine/monitor.png");
	private GuiButtonComponent selected;
	private GuiTemperature selectedTemp;

	public GuiMonitor(InventoryPlayer player, TileMachineMonitor tile) {
		super(new ContainerMonitor(player, tile), player, tile);

	}

	@Override
	protected ResourceLocation getTexture() {

		return MONITOR_TEXTURE;
	}

	@Override
	public void initGui() {

		super.initGui();

		selectedTemp = new GuiTemperature(0, guiLeft + 161, guiTop + 8, 7, 49,
				null, false, tile.getThermometerTier());
		selected = new GuiButtonComponent(ForgeDirection.UNKNOWN,
				guiLeft + 131, guiTop + 26, 16, 16, null,
				tile.getThermometerTier());
		buttonList.add(selectedTemp);
		buttonList.add(selected);
		ForgeDirection front;
		double dx = player.player.posX - tile.xCoord;
		double dz = player.player.posZ - tile.zCoord;
		if (Math.abs(dz) > Math.abs(dx)) {
			//in z direction
			if (dz > 0) { // +ve z
				front = ForgeDirection.SOUTH;
			} else { // -ve z
				front = ForgeDirection.NORTH;
			}
		} else {
			//in x direction
			if (dx > 0) { // +ve x
				front = ForgeDirection.EAST;
			} else {  // -ve x
				front = ForgeDirection.WEST;
			}
		}

		createButton(front, 80, 26);
		createButton(front.getRotation(ForgeDirection.UP), 62, 26);
		createButton(front.getRotation(ForgeDirection.DOWN), 98, 26);

		createButton(ForgeDirection.UP, 80, 8);
		createButton(ForgeDirection.DOWN, 80, 44);

		createButton(front.getOpposite(), 98, 44);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_,
				p_146976_2_,
				p_146976_3_);

	}

	@Override
	protected void drawHoveringText(List tooltip, int x, int y,
			FontRenderer font) {

		super.drawHoveringText(tooltip, x, y, font);
	}

	private void createButton(ForgeDirection dir, int x, int y) {
		TileEntity buttonTile = player.player.worldObj
				.getTileEntity(tile.xCoord + dir.offsetX, tile.yCoord
						+ dir.offsetY, tile.zCoord + dir.offsetZ);

		if (buttonTile instanceof TileMachineComponent) {
			buttonList.add(new GuiButtonComponent(dir, guiLeft + x, guiTop + y,
					16, 16, (TileMachineComponent) buttonTile, tile
							.getThermometerTier()));

		}

	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof GuiButtonComponent) {
			GuiButtonComponent bc = (GuiButtonComponent) button;
			if (bc.id != ForgeDirection.UNKNOWN.ordinal()) {
				selected.setTile(bc.tile);
				selectedTemp.tile = bc.tile;
			}
		}
	}

}
