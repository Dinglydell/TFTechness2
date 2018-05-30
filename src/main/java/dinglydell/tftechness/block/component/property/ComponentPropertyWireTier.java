package dinglydell.tftechness.block.component.property;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineRF;
import dinglydell.tftechness.tileentities.TileMachineRF.WireTier;

public class ComponentPropertyWireTier extends ComponentProperty<WireTier> {

	public ComponentPropertyWireTier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected NBTBase getTag(WireTier object) {

		return new NBTTagByte((byte) object.ordinal());
	}

	@Override
	public String getDisplayString(NBTTagCompound tag) {
		WireTier wire = WireTier.values()[tag.getByte(name)];
		return wire.name().toUpperCase() + " - " + wire.transferRate + "RF/t";
	}

	@Override
	public void setTileEntityValues(TileMachineComponent tile,
			NBTTagCompound tag) {
		((TileMachineRF) tile)
				.setWireTier(WireTier.values()[tag.getByte(name)]);

	}

	@Override
	public void writeNBTFromTileEntityValues(TileMachineComponent tile,
			NBTTagCompound nbt) {
		nbt.setByte(name, (byte) ((TileMachineRF) tile).getWireTier().ordinal());

	}
}
