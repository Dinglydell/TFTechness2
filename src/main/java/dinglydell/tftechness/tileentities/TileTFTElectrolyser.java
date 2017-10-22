package dinglydell.tftechness.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import blusunrize.immersiveengineering.common.IEContent;

import com.bioxx.tfc.api.TFCItems;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.item.TFTItems;
import dinglydell.tftechness.multiblock.IMultiblockTFT;
import dinglydell.tftechness.multiblock.MultiblockElectrolyser;
import dinglydell.tftechness.util.OreDict;

public class TileTFTElectrolyser extends TileTFTMachineBase {

	private static final int ELECTRODE_SLOT_A = 0;
	private static final int ELECTRODE_SLOT_B = 1;
	private static final int ALUMINA_SLOT = 2;
	private static final int REDSTONE_SLOT = 3;
	private static final int MOLD_SLOT = 4;

	public TileTFTElectrolyser() {
		inventory = new ItemStack[5];
	}

	@Override
	public boolean openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Electrolyser.ordinal(),
				world,
				getMasterX(),
				getMasterY(),
				getMasterZ());
		return true;

	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return "Electrolyser";
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {

		return 2;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot == ELECTRODE_SLOT_A || slot == ELECTRODE_SLOT_B) {
			return item.getItem() == IEContent.itemGraphiteElectrode;
		}
		if (slot == ALUMINA_SLOT) {
			return item.getItem() == TFTItems.alumina;
		}
		if (slot == REDSTONE_SLOT) {
			return OreDict.itemMatches(item, "dustRedstone");
		}
		if (slot == MOLD_SLOT) {
			return item.getItem() == TFCItems.ceramicMold
					|| item.getItem() == TFTItems.unshaped.get("Aluminium");
		}
		return false;
	}

	@Override
	public IMultiblockTFT getMultiblock() {
		return MultiblockElectrolyser.instance;
	}

}
