package dinglydell.tftechness.block.component.property;

import net.minecraft.util.StatCollector;

public class ComponentPropertyPowerType extends ComponentProperty<PowerType> {
	public ComponentPropertyPowerType(String name) {
		super(name);
	}

	@Override
	protected String getDisplayValue(PowerType value) {
		return StatCollector.translateToLocal("info.powertype." + value.name
				+ ".name")
				+ " - +" + value.tierEffect + " anvil tiers";
	}

}
