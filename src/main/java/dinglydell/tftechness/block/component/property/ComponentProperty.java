package dinglydell.tftechness.block.component.property;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import dinglydell.tftechness.tileentities.TileMachineComponent;

/**
 * A property of a component - the base class assumes they are floats for NBT
 * purposes but the type can be changed by overriding
 */
public abstract class ComponentProperty<T> {

	public static ComponentProperty WIRE_TIER = new ComponentPropertyWireTier(
			"tier");

	public static ComponentProperty CONDUCTIVITY = new ComponentPropertyConductivity(
			"conductivity");

	public final String name;

	public ComponentProperty(String name) {
		this.name = name;
	}

	public String getDisplayString(NBTTagCompound nbt) {
		return nbt.getTag(name).toString();
	}

	public void setNBT(NBTTagCompound nbt, T object) {// {
		nbt.setTag(name, getTag(object));
	}

	protected abstract NBTBase getTag(T object);

	//nbt.setFloat(name, (Float) object);

	//}

	public abstract void setTileEntityValues(TileMachineComponent tile,
			NBTTagCompound stack);

	public abstract void writeNBTFromTileEntityValues(
			TileMachineComponent tile, NBTTagCompound nbt);

}
