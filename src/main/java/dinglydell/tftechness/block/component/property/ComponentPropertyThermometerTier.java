package dinglydell.tftechness.block.component.property;

import dinglydell.tftechness.block.component.property.ComponentPropertyThermometerTier.ThermometerTier;
import dinglydell.tftechness.util.StringUtil;

public class ComponentPropertyThermometerTier extends
		ComponentProperty<ThermometerTier> {

	public enum ThermometerTier {
		fuzzy, precise
	}

	public ComponentPropertyThermometerTier(String name) {
		super(name);
	}

	@Override
	protected String getDisplayValue(ThermometerTier tier) {
		return StringUtil.capitaliseFirst(tier.name());
	}

}
