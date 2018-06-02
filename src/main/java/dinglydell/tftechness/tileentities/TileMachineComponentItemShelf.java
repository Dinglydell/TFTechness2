package dinglydell.tftechness.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.api.Food;
import com.bioxx.tfc.api.HeatIndex;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFC_ItemHeat;
import com.bioxx.tfc.api.Events.ItemCookEvent;
import com.bioxx.tfc.api.Interfaces.ICookableFood;
import com.bioxx.tfc.api.Interfaces.IFood;
import com.bioxx.tfc.api.Interfaces.ISmeltable;

import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.fluid.FluidMoltenMetal;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;
import dinglydell.tftechness.item.TFTItemPropertyRegistry;

public class TileMachineComponentItemShelf extends TileMachineInventory {

	private static final float SPECIFIC_HEAT = 1000;
	/**
	 * The amount the temperature is divided by to convert to cooking progress -
	 * for TFC machines it's 700 - note that conductivity is also a factor for
	 * TFT machines hence it is lower here
	 */
	private static final float TEMP_COOK_RATIO = 200f;
	private static final int[] RF_TASTE_PROFILE = new int[] {/* Sweet */-5,/* Sour */
	1,/* Salty */
	0,/* Bitter */
	1,/* Savory */
	1 };

	//private static final float MAX_COOK = 2f;
	public TileMachineComponentItemShelf() {
		inventory = new ItemStack[9];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			float temp = TFC_Climate.getHeightAdjustedTemp(worldObj,
					xCoord,
					yCoord,
					zCoord);
			float enviroDecay = 0;
			// If ambient temperature is below 0, not much can be done as TFC is
			// hardcoded to have no decay - even if internal temperature is greater.
			if (temp > 0) {
				enviroDecay = TFC_Core.getEnvironmentalDecay(getTemperature())
						/ TFC_Core.getEnvironmentalDecay(temp);
				// } else {
				// enviroDecay =
				// TFC_Core.getEnvironmentalDecay(internalTemperature);
			}
			TFC_Core.handleItemTicking(this,
					worldObj,
					xCoord,
					yCoord,
					zCoord,
					enviroDecay);
			for (int i = 0; i < inventory.length; i++) {
				ItemStack stack = inventory[i];

				if (stack != null) {
					float itemTemp = TFC_ItemHeat.getTemp(stack);
					if (temp > 0) {
						itemTemp += TFC_ItemHeat.getTempDecrease(stack);
					}
					HeatRegistry manager = HeatRegistry.getInstance();
					HeatIndex index = manager.findMatchingIndex(stack);

					if (index != null) {// && index.hasOutput()) {
						if (stack.getItem() instanceof IFood) {
							//only cook the food if it's greater than the default maximum ambient temperature in TFC
							if (temperature > TFC_Climate.getMaxTemperature()) {
								float cook = Food.getCooked(stack)
										+ Math.max(0, temperature
												* getConductivity()
												/ TEMP_COOK_RATIO);
								Food.setCooked(stack, cook);
								itemTemp = cook;
								// from TEGrill:
								if (Food.isCooked(stack)) {
									int[] cookedTasteProfile = new int[] { 0,
											0,
											0,
											0,
											0 };

									Random r = new Random(
											((ICookableFood) stack.getItem())
													.getFoodID()
													+ (((int) Food
															.getCooked(stack) - 600) / 120));
									cookedTasteProfile[0] = r.nextInt(31) - 15;
									cookedTasteProfile[1] = r.nextInt(31) - 15;
									cookedTasteProfile[2] = r.nextInt(31) - 15;
									cookedTasteProfile[3] = r.nextInt(31) - 15;
									cookedTasteProfile[4] = r.nextInt(31) - 15;
									Food.setCookedProfile(stack,
											cookedTasteProfile);

									Food.setFuelProfile(stack, RF_TASTE_PROFILE);
								}
							} else { //no cook food!
								itemTemp = 0;
							}
						} else {
							float change = (getConductivity() + AIR_CONDUCTIVITY)
									* 0.5f * (temperature - itemTemp);
							itemTemp += change;
							//						float energy = change * index.specificHeat * itemMass;
							temperature -= change * index.specificHeat
									/ SPECIFIC_HEAT;
							//temperature = 0;
							//itemTemp = 0;
						}
						TFC_ItemHeat.setTemp(stack, itemTemp);
						if (itemTemp > index.meltTemp) {

							ItemStack output = index.getOutput(stack,
									new Random());
							if (stack.getItem() instanceof IFood) {
								ItemCookEvent eventMelt = new ItemCookEvent(
										stack, output, this);
								MinecraftForge.EVENT_BUS.post(eventMelt);
								output = eventMelt.result;

								inventory[i] = output;
								if (output != null
										&& manager.findMatchingIndex(output) != null) {
									TFC_ItemHeat.setTemp(output, temp);
								}
							} else if (stack.getItem() instanceof ISmeltable) {
								ISmeltable smelt = (ISmeltable) stack.getItem();
								//TODO: molds or tanks and stuff like that
								//MELT!
								List<TileMachineComponentTank> neighbourTanks = new ArrayList<TileMachineComponentTank>();
								for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) { // try and move the contents into neighbouring tanks
									TileEntity tile = worldObj
											.getTileEntity(xCoord + dir.offsetX,
													yCoord + dir.offsetY,
													zCoord + dir.offsetZ);
									if (tile instanceof TileMachineComponentTank) {
										neighbourTanks
												.add((TileMachineComponentTank) tile);

									}
								}
								FluidMoltenMetal molten = TFTItemPropertyRegistry
										.getMolten(stack);
								for (TileMachineComponentTank tank : neighbourTanks) { //spread out the molten metal

									int amt = smelt.getMetalReturnAmount(stack)
											/ neighbourTanks.size();
									FluidStack moltenStack = molten
											.createStack(smelt
													.getMetalReturnAmount(stack)
													/ neighbourTanks.size(),
													temperature);
									tank.fill(ForgeDirection.UNKNOWN,
											moltenStack,
											true);
								}
								if (neighbourTanks.size() != 0
										&& smelt.getMetalReturnAmount(stack)
												% neighbourTanks.size() != 0) { //add remainder
									FluidStack moltenStack = molten
											.createStack(smelt
													.getMetalReturnAmount(stack)
													% neighbourTanks.size(),
													temperature);
									neighbourTanks.get(0)
											.fill(ForgeDirection.UNKNOWN,
													moltenStack,
													true);
								}

								inventory[i] = null;

							} else if (output.getItem() != stack.getItem()) {
								inventory[i] = output;
							}
						}

					}
				}
			}
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return HeatRegistry.getInstance().findMatchingIndex(stack) != null;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {

		super.readFromNBT(data);
		//inventory
		NBTTagList invTag = data.getTagList("Items", 10);

		for (int i = 0; i < invTag.tagCount(); ++i) {
			NBTTagCompound itemTag = invTag.getCompoundTagAt(i);
			byte b0 = itemTag.getByte("Slot");

			if (b0 >= 0 && b0 < inventory.length) {
				inventory[b0] = ItemStack.loadItemStackFromNBT(

				itemTag);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {

		super.writeToNBT(data);
		//inventory
		NBTTagList invTag = new NBTTagList();

		for (int i = 0; i < inventory.length; ++i) {
			if (inventory[i] != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(itemTag);
				invTag.appendTag(itemTag);
			}
		}

		data.setTag("Items", invTag);
	}

	public boolean openGui(World world, EntityPlayer player) {
		//if (!hasMaster()) {
		//	return false;
		//}
		player.openGui(TFTechness2.instance,
				TFTGuis.ItemShelf.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		//getMasterX(),
		//getMasterY(),
		//getMasterZ());
		return true;
	}

}
