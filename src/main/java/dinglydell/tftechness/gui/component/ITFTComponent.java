package dinglydell.tftechness.gui.component;

import java.util.List;

public interface ITFTComponent {
	void addTooltip(List<String> tooltip);

	void draw();

	boolean isHovering(int x, int y);
}
