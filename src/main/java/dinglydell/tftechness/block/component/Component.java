package dinglydell.tftechness.block.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import scala.actors.threadpool.Arrays;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.block.component.property.ComponentPropertySet;
import dinglydell.tftechness.item.TFTPropertyRegistry;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.util.ItemUtil;

public class Component {

	public static List<Component> components = new ArrayList<Component>();

	public static void registerComponent(Component c) {
		components.add(c);
	}

	public final String name;
	protected List<String> tooltips = new ArrayList<String>();

	//TODO: make a ComponentPropertySet class, which contains the array of properties as well as data such as whether these properties should use the primary or secondary material
	/** The list of sets of properties that come from the same material */
	public List<ComponentPropertySet> propertySets = new ArrayList<ComponentPropertySet>();

	/**
	 * Properties that should show up in tooltips - those that are actually
	 * relevant to this component
	 */
	protected HashSet<ComponentProperty> relevantProperties = new HashSet<ComponentProperty>();

	private IIcon[] icons;
	private Object[] recipe;
	private Class<? extends TileMachineComponent> type;
	private List<String> iconStrs = new ArrayList<String>();
	private int numPrimary;
	private int numSecondary;

	/**
	 * IMPORTANT: components are made up of swappable materials. Each letter in
	 * the alphabet refers to a material in order, where a capital is the
	 * primary material and lower case is the secondary Note: in all cases so
	 * far the first property (A) will be the base component. Eg: A - iron
	 * double sheet, a - iron sheet (single).
	 */
	public Component(String name, Class<? extends TileMachineComponent> type,
			Object... recipe) {
		this.name = name;
		this.recipe = recipe;
		this.type = type;
		tooltips.add("info.machine." + name + ".tooltip");

		iconStrs.add(TFTechness2.MODID + ":machine/" + name.toLowerCase());
		//universal
		registerPropertySet(ComponentPropertySet.BASE, false);

		for (Object o : recipe) {
			if (o instanceof String) {
				String s = (String) o;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == 'A') {
						numPrimary++;
					} else if (s.charAt(i) == 'a') {
						numSecondary++;
					}
				}
				//if (s.('A')) {

				//numPrimary++;
				//} else if (o.equals('a')) {
				//					numSecondary++;
				//			}
			}
		}
	}

	//public Component(String name, String tooltip, Object... recipe) {
	//	this(name, recipe);
	//	tooltips.add(tooltip);
	//}

	public Component registerComponentTooltip(String tooltip) {
		tooltips.add(tooltip);
		return this;
	}

	public Component registerAdditionalIcon(String name) {
		iconStrs.add(TFTechness2.MODID + ":machine/" + this.name.toLowerCase()
				+ "_" + name);
		return this;
	}

	/**
	 * Registers a set of properties given by a single material
	 * 
	 * @param allRelevant
	 *            If true, all properties will be displayed in tooltips.
	 */
	public Component registerPropertySet(ComponentPropertySet set,
			boolean allRelevant) {
		propertySets.add(set);
		if (allRelevant) {
			for (ComponentProperty p : set.properties) {
				relevantProperties.add(p);
			}
		}

		return this;
	}

	/** Registers a set of properties given by a single material */
	public Component registerPropertySet(ComponentPropertySet set) {
		return registerPropertySet(set, true);
	}

	public Component setPropertyRelevant(ComponentProperty property) {
		relevantProperties.add(property);
		return this;
	}

	public Component setAllBasePropertiesRelevant() {
		for (ComponentProperty p : ComponentPropertySet.BASE.properties) {
			relevantProperties.add(p);
		}
		return this;
	}

	public void addTooltip(List list, NBTTagCompound nbt) {
		if (!GuiScreen.isShiftKeyDown()) {
			list.add(EnumChatFormatting.RED.toString()
					+ EnumChatFormatting.ITALIC.toString()
					+ "Hold <SHIFT> for details");
			return;
		}
		for (String tooltip : tooltips) {
			list.add(EnumChatFormatting.RED.toString()
					+ StatCollector.translateToLocal(tooltip));
		}
		if (nbt == null) {
			return;
		}
		//for (ComponentPropertySet set : propertySets) {
		for (ComponentProperty prop : relevantProperties) {
			list.add(EnumChatFormatting.DARK_AQUA.toString()
					+ StatCollector.translateToLocal("info.machine.property."
							+ prop.name) + " "
					+ EnumChatFormatting.RED.toString()
					+ prop.getDisplayString(nbt));
		}
		//	}

	}

	//TODO: secondary materials for properties
	public IRecipe getRecipe(List<ComponentMaterial> materials) {
		//propertySets
		ItemStack wall = BlockTFTComponent.getBlockWithNBT(this, materials);
		List<Object> specificRecipe = new ArrayList<Object>();

		specificRecipe.addAll(Arrays.asList(recipe));
		int offset = (int) 'a';
		for (int i = 0; i < materials.size(); i++) {
			ComponentMaterial m = materials.get(i);

			specificRecipe.add((char) (offset + i));
			specificRecipe.add(m.shelfMaterial);
			specificRecipe.add(Character.toUpperCase((char) (offset + i)));
			specificRecipe.add(m.material);
		}

		//specificRecipe.add('m');
		//specificRecipe.add(baseMaterial);

		return new ShapedOreRecipe(wall, specificRecipe.toArray());
	}

	public void registerIcons(IIconRegister register) {
		icons = new IIcon[iconStrs.size()];
		for (int i = 0; i < iconStrs.size(); i++) {
			icons[i] = register.registerIcon(iconStrs.get(i));
		}
	}

	public IIcon getIcon(int index) {
		return icons[index];
	}

	public TileMachineComponent createNewTileEntity(World worldIn, int meta) {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();

		}
		return null;
	}

	//public void readPropertiesFromNBT(TileMachineComponent tile,
	//		NBTTagCompound nbt) {
	//	for (ComponentPropertySet propSet : propertySets) {
	//		for (ComponentProperty prop : propSet.properties) {
	//			prop.setTileEntityValues(tile, nbt);
	//		}
	//	}
	//}

	//public void writePropertiesToNBT(TileMachineComponent tile,
	//		NBTTagCompound nbt) {
	//	for (ComponentPropertySet propSet : propertySets) {
	//		tile.writeComponentSetToNBT(nbt, propSet);
	//		//for (ComponentProperty prop : propSet) {
	//		//prop.writeNBTFromTileEntityValues(tile, nbt);
	//		//}
	//	}
	//}

	public void setTileEntityValues(TileMachineComponent tile, ItemStack stack) {
		for (ComponentPropertySet propSet : propertySets) {
			for (ComponentProperty prop : propSet.properties) {
				tile.setProperty(prop, stack.getTagCompound());
				//prop.setTileEntityValues(tile, stack.getTagCompound());
			}
		}
		tile.initialiseProperties();
	}

	public Class<? extends TileMachineComponent> getType() {
		return type;
	}

	/** Gets the volume (m^3) of base material. Used when it the component melts */
	public float getBaseMaterialAmount(Object primary, Object secondary) {
		ItemStack p = ItemUtil.getStack(primary);
		ItemStack s = ItemUtil.getStack(secondary);

		return numPrimary * TFTPropertyRegistry.getVolumeDensity(p)
				+ numSecondary * TFTPropertyRegistry.getVolumeDensity(s);
	}
}
