package dinglydell.tftechness.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import dinglydell.tftechness.tileentities.TileMachineComponentTurbine;
import dinglydell.tftechness.util.StringUtil;

public class GuiMachineTurbine extends GuiMachineTank {

	private TileMachineComponentTurbine tile;

	public GuiMachineTurbine(InventoryPlayer player,
			TileMachineComponentTurbine tile) {
		super(player, tile);
		this.tile = tile;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {

		super.drawGuiContainerBackgroundLayer(p_146976_1_,
				p_146976_2_,
				p_146976_3_);

		String str = StatCollector.translateToLocal(StringUtil.prefixify(tile
				.getRPS()) + "Hz");
		fontRendererObj.drawString(str,
				guiLeft + 97 - fontRendererObj.getStringWidth(str) / 2,
				guiTop + 28,
				0xFFFFFF);
	}

}
