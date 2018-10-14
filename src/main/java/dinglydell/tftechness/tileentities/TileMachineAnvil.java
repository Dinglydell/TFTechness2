package dinglydell.tftechness.tileentities;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import com.bioxx.tfc.Core.TFC_Sounds;
import com.bioxx.tfc.api.HeatRegistry;
import com.bioxx.tfc.api.TFC_ItemHeat;
import com.bioxx.tfc.api.Crafting.AnvilManager;
import com.bioxx.tfc.api.Crafting.AnvilRecipe;
import com.bioxx.tfc.api.Crafting.AnvilReq;
import com.bioxx.tfc.api.Events.AnvilCraftEvent;

import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertyAction.AnvilAction;
import dinglydell.tftechness.block.component.property.PowerType;

public class TileMachineAnvil extends TileMachineInventory {

	/** how many ticks does it take to process an item? */
	protected static final int WORK_TICK_THRESHHOLD = 20;
	public static final String ITEM_CRAFT_VALUE = "itemCraftingValue";
	private static final String ITEM_CRAFTING_RULE_1 = "itemCraftingRule1";
	private static final String ITEM_CRAFTING_RULE_2 = "itemCraftingRule2";
	private static final String ITEM_CRAFTING_RULE_3 = "itemCraftingRule2";
	//	protected ItemStack workingOn;
	protected int workTicks = 0;
	protected static final int WORKING_SLOT_A = 0;
	protected static final int WORKING_SLOT_B = 1;
	protected static final int FLUX_SLOT = 2;
	public String plan;

	protected byte[] lastRules = new byte[3];
	private int craftValue;

	public TileMachineAnvil() {
		inventory = new ItemStack[3];
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		if (inventory[WORKING_SLOT_A] != null) {
			doWorkOnItem();
			return;
		}
		if (blockMetadata == -1) {
			return;
		}
		ForgeDirection facing = ForgeDirection.values()[blockMetadata];

		List items = worldObj.getEntitiesWithinAABB(EntityItem.class,
				getRenderBoundingBox().offset(facing.offsetX * 0.5,
						facing.offsetY * 0.5,
						facing.offsetZ * 0.5));

		for (Object o : items) {
			if (o instanceof EntityItem) {
				EntityItem item = (EntityItem) o;
				inventory[WORKING_SLOT_A] = item.getEntityItem();
				item.setDead();
				if (plan == null) {
					AnvilManager am = AnvilManager.getInstance();
					Set<String> plans = am.getPlans().keySet();
					for (String p : plans) {
						AnvilRecipe ar = am.findMatchingRecipe(new AnvilRecipe(
								inventory[WORKING_SLOT_A], null, (String) p,
								false, getAnvilTier()));

						if (ar != null) { // if we have a match..
							if (plan == null) { // set the plan if there isn't one
								plan = p;
							} else { // if there is one then don't autoselect a plan
								plan = null;
								break;
							}
						}
					}
				}
				return;
			}
		}
	}

	private int getAnvilTier() {
		AnvilReq tier = (AnvilReq) materials.get(ComponentProperty.ANVIL_TIER).validFor
				.get(ComponentProperty.ANVIL_TIER);
		PowerType power = (PowerType) materials
				.get(ComponentProperty.ANVIL_POWER).validFor
				.get(ComponentProperty.ANVIL_POWER);
		return tier.Tier + power.getTierEffect();
	}

	public AnvilAction getAction() {
		return (AnvilAction) materials.get(ComponentProperty.ANVIL_ACTION_TYPE).validFor
				.get(ComponentProperty.ANVIL_ACTION_TYPE);
	}

	protected void doWorkOnItem() {
		workTicks++;
		if (workTicks >= WORK_TICK_THRESHHOLD) {

			if (HeatRegistry.getInstance()
					.isTemperatureWorkable(inventory[WORKING_SLOT_A])) {
				setItemCraftingValue(getAction());
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, TFC_Sounds.METALIMPACT, 0.1f, 0.1F + (worldObj.rand.nextFloat()/4));
				checkRecipes();
			}
			spitItem();
			workTicks = 0;
		}

	}

	private void spitItem() {
		ForgeDirection spitDir = ForgeDirection.values()[blockMetadata]
				.getOpposite();
		EntityItem item = new EntityItem(worldObj, xCoord + 0.5 + 0.5
				* spitDir.offsetX, yCoord + 0.5 + 0.5 * spitDir.offsetY, zCoord
				+ 0.5 + 0.5 * spitDir.offsetZ, inventory[WORKING_SLOT_A]);
		item.motionX = spitDir.offsetX * 0.05;
		item.motionY = spitDir.offsetY * 0.05;
		item.motionZ = spitDir.offsetZ * 0.05;

		worldObj.spawnEntityInWorld(item);
		inventory[WORKING_SLOT_A] = null;

	}

	protected void checkRecipes() {
		Object[] output = AnvilManager.getInstance()
				.findCompleteRecipe(new AnvilRecipe(inventory[WORKING_SLOT_A],
						null, plan, false, getAnvilTier()),
						getRules());
		if (output != null && output[0] != null && output[1] != null) {
			AnvilRecipe recipe = (AnvilRecipe) output[0];
			ItemStack outStack = (ItemStack) output[1];
			AnvilCraftEvent eventCraft = new AnvilCraftEvent(null, this,
					inventory[WORKING_SLOT_A], null, outStack);
			MinecraftForge.EVENT_BUS.post(eventCraft);
			if (!eventCraft.isCanceled()) {
				outStack = eventCraft.result;
				TFC_ItemHeat.setTemp(outStack,
						TFC_ItemHeat.getTemp(inventory[WORKING_SLOT_A]));
				inventory[WORKING_SLOT_A] = outStack;
				inventory[WORKING_SLOT_B] = null;
			}
		}

	}

	private int[] getRules() {

		return new int[] { lastRules[0], lastRules[1], lastRules[2] };
	}

	public void setItemCraftingValue(AnvilAction action) {
		if (inventory[WORKING_SLOT_A] != null) {
			NBTTagCompound tag = null;
			if (inventory[WORKING_SLOT_A].hasTagCompound()) {
				tag = inventory[WORKING_SLOT_A].getTagCompound();
				if (tag.hasKey(ITEM_CRAFT_VALUE)) {
					short craftingValue = tag.getShort(ITEM_CRAFT_VALUE);
					craftValue = craftingValue + action.value;

				} else {
					craftValue = action.value;

				}

			} else {
				tag = new NBTTagCompound();
				craftValue = action.value;

			}
			tag.setShort(ITEM_CRAFT_VALUE, (short) Math.max(0, craftValue));
			inventory[WORKING_SLOT_A].setTagCompound(tag);

			if (tag.hasKey(ITEM_CRAFTING_RULE_1)) {
				lastRules[1] = tag.getByte(ITEM_CRAFTING_RULE_1);
			}
			if (tag.hasKey(ITEM_CRAFTING_RULE_2)) {
				lastRules[2] = tag.getByte(ITEM_CRAFTING_RULE_2);
			}

			lastRules[0] = action.rule;
			tag.setByte(ITEM_CRAFTING_RULE_1, lastRules[0]);
			tag.setByte(ITEM_CRAFTING_RULE_2, lastRules[1]);
			tag.setByte(ITEM_CRAFTING_RULE_3, lastRules[2]);
			//	return true;
		}

		//return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public IIcon getIcon(int side) {
		ForgeDirection dir = ForgeDirection.values()[side];
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return super.getIcon((dir.ordinal() == blockMetadata || dir
				.getOpposite().ordinal() == blockMetadata) ? 1 : 0);
	}
}
