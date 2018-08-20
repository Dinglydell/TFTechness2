package dinglydell.tftechness.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import dinglydell.tftechness.block.component.BlockTFTComponent;
import dinglydell.tftechness.block.component.Component;

public class TFTBlocks {
	public static Block metalSheet;
	//	public static Block machine;
	public static Map<String, Block> moltenMetal = new HashMap<String, Block>();
	public static Block crops;
	public static Block barrel;
	public static ItemStack treatedBarrel;
	//public static Block machineComponent;
	public static Map<Component, BlockTFTComponent> components = new HashMap<Component, BlockTFTComponent>();
}
