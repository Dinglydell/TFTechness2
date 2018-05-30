package dinglydell.tftechness.block.component.property;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import dinglydell.tftechness.tileentities.TileMachineComponent;

public class ComponentPropertyConductivity extends ComponentProperty<Float> {

	public ComponentPropertyConductivity(String name) {
		super(name);

	}

	@Override
	protected NBTBase getTag(Float object) {
		return new NBTTagFloat(object);

	}

	@Override
	public String getDisplayString(NBTTagCompound tag) {

		return (Math.round(tag.getFloat(name) * 1000) / 10f) + "%";
	}

	@Override
	public void setTileEntityValues(TileMachineComponent tile,
			NBTTagCompound tag) {
		tile.setConductivity(tag.getFloat(name));
	}

	@Override
	public void writeNBTFromTileEntityValues(TileMachineComponent tile,
			NBTTagCompound nbt) {
		nbt.setFloat(name, tile.getConductivity());

	}

}
