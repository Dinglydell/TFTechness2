package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;

public class TileMachineMonitor extends TileMachineComponent {
	public boolean openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Monitor.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}

	@Override
	public ThermometerTier getThermometerTier() {
		return (ThermometerTier) materials
				.get(ComponentProperty.THERMOMETER_TIER).validFor
				.get(ComponentProperty.THERMOMETER_TIER);
	}
}
