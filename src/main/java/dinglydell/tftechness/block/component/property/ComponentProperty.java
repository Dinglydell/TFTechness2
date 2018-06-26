package dinglydell.tftechness.block.component.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import dinglydell.tftechness.block.component.ComponentMaterial;

/**
 * A property of a component - the base class assumes they are floats for NBT
 * purposes but the type can be changed by overriding
 */
public abstract class ComponentProperty<T> {

	public static ComponentProperty WIRE_TIER = new ComponentPropertyWireTier(
			"tier");

	/** The thermal conductivity - measured as a number between 0 and 1. */
	public static ComponentProperty CONDUCTIVITY = new ComponentPropertyPercent(
			"conductivity");

	/** The specific heat of a component - measured in J/(kgK) */
	public static ComponentProperty SPECIFIC_HEAT = new ComponentPropertyFloat(
			"specificHeat", "J/(kgK)");

	public final String name;

	public ComponentProperty(String name) {
		this.name = name;
	}

	public String getDisplayString(NBTTagCompound nbt) {
		ComponentMaterial m = ComponentMaterial
				.getMaterial(nbt.getString(name));
		if (m == null) {
			return StatCollector.translateToLocal("info.machine.brokendata");
		}
		//getDisplayValueFromMaterial(m);
		return getDisplayValue((T) m.validFor.get(this)) + " ("
				+ StatCollector.translateToLocal("metal." + m.name + ".name")
				+ ")";
	}

	protected abstract String getDisplayValue(T value);

	//public void setNBT(NBTTagCompound nbt, T object) {// {
	//	nbt.setTag(name, getTag(object));
	//}
	//
	//protected abstract NBTBase getTag(T object);

	//nbt.setFloat(name, (Float) object);

	//}

	//public abstract void setTileEntityValues(TileMachineComponent tile,
	//	NBTTagCompound stack);

	//public abstract void writeNBTFromTileEntityValues(
	//		TileMachineComponent tile, NBTTagCompound nbt);

}
