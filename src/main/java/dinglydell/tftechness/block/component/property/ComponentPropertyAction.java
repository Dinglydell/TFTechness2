package dinglydell.tftechness.block.component.property;

import net.minecraft.util.StatCollector;
import dinglydell.tftechness.block.component.property.ComponentPropertyAction.AnvilAction;

public class ComponentPropertyAction extends ComponentProperty<AnvilAction> {
	public ComponentPropertyAction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	//follows the way they're named in TFC (with regards to lang)
	public enum AnvilAction {
		LightHit, MediumHit, HeavyHit, Draw, Punch, Bend, Upset, Shrink

	}

	@Override
	protected String getDisplayValue(AnvilAction value) {
		return StatCollector.translateToLocal("gui.Anvil." + value.toString());
	}

}
