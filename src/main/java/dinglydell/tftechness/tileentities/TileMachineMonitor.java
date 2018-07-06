package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import dinglydell.tftechness.TFTechness2;
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

}
