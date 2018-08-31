package dinglydell.tftechness.block.component.property;

import com.bioxx.tfc.api.Crafting.AnvilReq;

public class ComponentPropertyAnvilTier extends ComponentProperty<AnvilReq> {

	public ComponentPropertyAnvilTier(String name) {
		super(name);
	}

	@Override
	protected String getDisplayValue(AnvilReq req) {

		return "" + req.Tier;
	}

}
