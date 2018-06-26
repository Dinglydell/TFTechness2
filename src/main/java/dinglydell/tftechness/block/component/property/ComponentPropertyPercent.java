package dinglydell.tftechness.block.component.property;


public class ComponentPropertyPercent extends ComponentProperty<Float> {

	public ComponentPropertyPercent(String name) {
		super(name);

	}

	//@Override
	//protected NBTBase getTag(Float object) {
	//	return new NBTTagFloat(object);
	//
	//}
	//
	//@Override
	//public String getDisplayString(NBTTagCompound tag) {
	//
	//	return (Math.round(tag.getFloat(name) * 1000) / 10f) + "%";
	//}

	//@Override
	//public void setTileEntityValues(TileMachineComponent tile,
	//		NBTTagCompound tag) {
	//	tile.setConductivity(tag.getFloat(name));
	//}

	//@Override
	//public void writeNBTFromTileEntityValues(TileMachineComponent tile,
	//		NBTTagCompound nbt) {
	//	nbt.setFloat(name, tile.getConductivity());
	//
	//}

	@Override
	protected String getDisplayValue(Float value) {
		return (Math.round(value * 1000) / 10f) + "%";
	}

}
