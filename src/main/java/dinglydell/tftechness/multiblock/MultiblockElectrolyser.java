package dinglydell.tftechness.multiblock;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.item.ItemMeta;
import dinglydell.tftechness.item.TFTMeta;
import dinglydell.tftechness.util.ItemUtil;

public class MultiblockElectrolyser implements IMultiblock {

	public static MultiblockElectrolyser instance = new MultiblockElectrolyser();
	static ItemStack[][][] structure = new ItemStack[][][] { { {
			//bottom layer
	TFTMeta.ieSteelScaffolding,
			TFTMeta.ieLightEngineering,
			TFTMeta.ieSteelBlock,
			TFTMeta.ieLightEngineering,
			TFTMeta.ieHeavyEngineering },
			{ TFTMeta.ieSteelBlock,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelBlock },
			{ TFTMeta.ieSteelBlock,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelBlock },
			{ TFTMeta.ieSteelBlock,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelSlab,
					TFTMeta.ieSteelBlock },
			{ TFTMeta.ieSteelScaffolding,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieSteelBlock,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieHeavyEngineering } },

			//second layer
			{ { TFTMeta.ieSteelScaffolding,
					TFTMeta.ieHeavyEngineering,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieHeavyEngineering,
					TFTMeta.ieLightEngineering },
					{ TFTMeta.ieHeavyEngineering,
							null,
							null,
							null,
							TFTMeta.ieHeavyEngineering

					},
					{ TFTMeta.ieLightEngineering,
							null,
							null,
							null,
							TFTMeta.ieLightEngineering

					},
					{ TFTMeta.ieHeavyEngineering,
							null,
							null,
							null,
							TFTMeta.ieHeavyEngineering

					},
					{ TFTMeta.ieSteelScaffolding,
							TFTMeta.ieHeavyEngineering,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieHeavyEngineering,
							TFTMeta.ieLightEngineering }

			},
			//third layer
			{ { TFTMeta.ieSteelScaffolding,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieHeavyEngineering,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieHeavyEngineering },
					{ TFTMeta.ieLightEngineering,
							null,
							null,
							null,
							TFTMeta.ieLightEngineering

					},
					{ TFTMeta.ieHeavyEngineering,
							null,
							null,
							null,
							TFTMeta.ieHeavyEngineering

					},
					{ TFTMeta.ieLightEngineering,
							null,
							null,
							null,
							TFTMeta.ieLightEngineering

					},
					{ TFTMeta.ieSteelScaffolding,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieHeavyEngineering,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieHeavyEngineering } },
			//top layer
			{ { TFTMeta.ieSteelScaffolding,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieLightEngineering,
					TFTMeta.ieLightEngineering },
					{ TFTMeta.ieLightEngineering,
							TFTMeta.ieSteelSlab,
							TFTMeta.ieSteelSlab,
							TFTMeta.ieSteelSlab,
							TFTMeta.ieLightEngineering },
					{ TFTMeta.ieSteelBlock,
							TFTMeta.ieHeavyEngineering,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieHeavyEngineering,
							TFTMeta.ieSteelBlock },
					{ TFTMeta.ieLightEngineering,
							TFTMeta.ieSteelSlab,
							TFTMeta.ieSteelSlab,
							TFTMeta.ieSteelSlab,
							TFTMeta.ieLightEngineering },
					{ TFTMeta.ieSteelScaffolding,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieLightEngineering,
							TFTMeta.ieLightEngineering } } };
	private static ItemStack[] materials;

	static {
		Map<ItemMeta, ItemStack> items = new HashMap<ItemMeta, ItemStack>();
		for (ItemStack[][] plane : structure) {
			for (ItemStack[] line : plane) {
				for (ItemStack stack : line) {
					if (stack == null) {
						continue;
					}
					ItemMeta im = new ItemMeta(stack);
					if (!items.containsKey(im)) {
						items.put(im, ItemUtil.clone(stack, 0));
					}

					items.get(im).stackSize++;
				}
			}
		}

		materials = items.values()
				.toArray(new ItemStack[items.values().size()]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderFormedStructure() {

		return true;
	}

	@Override
	public boolean createStructure(World world, int x, int y, int z, int side,
			EntityPlayer player) {
		EnumFacing facing = EnumFacing.values()[side];
		if (isValidStructure(world, x, y, z, facing)) {
			return true;
		}
		return false;
	}

	private boolean isValidStructure(World world, int x, int y, int z,
			EnumFacing facing) {
		for (int hs = 0; hs < structure.length; hs++) {
			ItemStack[][] hPlane = structure[hs];
			int h = hs - 1;
			for (int ws = 0; ws < hPlane.length; ws++) {
				int w = ws - 2;
				ItemStack[] hwLine = hPlane[ws];
				for (int ds = 0; ds < hwLine.length; ds++) {
					ItemStack stack = hwLine[ds];
					int d = ds;
					int blockX = x + Math.abs(facing.getFrontOffsetZ()) * w
							- facing.getFrontOffsetX() * d;
					int blockY = y + h;
					int blockZ = z + Math.abs(facing.getFrontOffsetX()) * w
							- facing.getFrontOffsetZ() * d;

					Block block = world.getBlock(blockX, blockY, blockZ);
					int meta = world.getBlockMetadata(blockX, blockY, blockZ);
					if (stack == null) {
						if (block != Blocks.air) {
							return false;
						}
						continue;
					}
					if (!stack.isItemEqual(new ItemStack(block, 1, meta))) {
						return false;
					}

				}
			}
		}
		return true;

	}

	@Override
	public float getManualScale() {
		return 4;
	}

	@Override
	public ItemStack[][][] getStructureManual() {
		return structure;
	}

	@Override
	public ItemStack[] getTotalMaterials() {

		return materials;
	}

	@Override
	public String getUniqueName() {
		return TFTechness2.MODID + ":electrolyser";
	}

	@Override
	public boolean isBlockTrigger(Block block, int meta) {
		return new ItemStack(block, 1, meta)
				.isItemEqual(TFTMeta.ieLightEngineering);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean overwriteBlockRender(ItemStack arg0, int arg1) {

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFormedStructure() {
		// TODO Auto-generated method stub

	}

}
