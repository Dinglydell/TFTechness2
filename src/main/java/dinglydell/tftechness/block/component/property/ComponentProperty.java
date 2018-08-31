package dinglydell.tftechness.block.component.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.ComponentMaterial;

/**
 * A property of a component - the base class assumes they are floats for NBT
 * purposes but the type can be changed by overriding
 */
public abstract class ComponentProperty<T> {

	/** The type of power used by an anvil (typically steam vs rf) */
	public static final ComponentProperty ANVIL_POWER = new ComponentPropertyPowerType(
			"anvilPower");

	public static final ComponentProperty ANVIL_TIER = new ComponentPropertyAnvilTier(
			"anvilTier");

	public static final ComponentProperty ANVIL_ACTION_TYPE = new ComponentPropertyAction(
			"anvilAction");

	/** The maximum rotary speed the turbine can handle */
	public static ComponentProperty MAX_TURBINE_SPEED = new ComponentPropertyFloat(
			"turbineSpeed", "Hz");

	public static ComponentProperty WIRE_TIER = new ComponentPropertyWireTier(
			"tier");

	public static ComponentProperty THERMOMETER_TIER = new ComponentPropertyThermometerTier(
			"thermoTier");

	/** The thermal conductivity - measured as a number between 0 and 1. */
	public static ComponentProperty CONDUCTIVITY = new ComponentPropertyPercent(
			"conductivity");

	/** The specific heat of a component - measured in J/(kgK) */
	public static ComponentProperty SPECIFIC_HEAT = new ComponentPropertyFloat(
			"specificHeat", "J/(kgK)");
	/**
	 * The highest level of pressure this component can contain.
	 */
	public static ComponentProperty MAXIMUM_PRESSURE = new ComponentPropertyFloat(
			"maxPressure", "Pa");

	/**
	 * The highest temperature (celsius) this component can reach before it
	 * starts to break down
	 */
	public static ComponentProperty MAXIMUM_TEMPERATURE = new ComponentPropertyFloat(
			"maxTemperature", TFTechness2.degrees + "C", false);

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
