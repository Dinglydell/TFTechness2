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
		LightHit(-3, 0), MediumHit(-6, 0), HeavyHit(-9, 0), Draw(-15, 1), Punch(
				2, 3), Bend(7, 4), Upset(13, 5), Shrink(16, 6);

		public short value;
		public byte rule;

		private AnvilAction(int value, int rule) {
			this.value = (short) value;
			this.rule = (byte) rule;
		}

	}

	@Override
	protected String getDisplayValue(AnvilAction value) {
		return StatCollector.translateToLocal("gui.Anvil." + value.toString());
	}

}
