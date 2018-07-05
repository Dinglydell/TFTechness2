package dinglydell.tftechness.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import dinglydell.tftechness.tileentities.TileMachineComponent;
import dinglydell.tftechness.tileentities.TileMachineComponentTank;
import dinglydell.tftechness.util.StringUtil;

public class TFTWaila implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();
		if (te instanceof TileMachineComponent) {
			TileMachineComponent tile = (TileMachineComponent) te;
			float temp = accessor.getNBTData().getFloat("Temperature");
			float maxT = accessor.getNBTData().getFloat("MaxTemperature");
			if (maxT == 0) {
				maxT = Float.MAX_VALUE;
			}
			StringUtil.addTemperatureTooltip(currenttip, temp, maxT);

			if (te instanceof TileMachineComponentTank) {
				TileMachineComponentTank tank = (TileMachineComponentTank) te;
				if (tank.isSealed()) {
					currenttip
							.add(EnumChatFormatting.ITALIC
									+ StatCollector
											.translateToLocal("gui.machine.tank.sealed"));
				}
				float solids = tank.getTank().getSolidMass();
				if (solids != 0) {
					currenttip.add("Solids ("
							+ StringUtil.prefixify(solids * 1000) + "g)");
				}
				float fluids = tank.getTank().getFluidAmount();
				if (fluids != 0) {
					currenttip.add("Liquids ("
							+ StringUtil.prefixify(fluids * 0.001) + "B)");
				}
				double pressure = accessor.getNBTData().getFloat("Pressure");
				double maxP = accessor.getNBTData().getFloat("MaxPressure");
				if (maxP == 0) {
					maxP = Double.MAX_VALUE;
				}
				StringUtil.addPressureTooltip(currenttip,
						pressure,
						maxP,
						((TileMachineComponentTank) te)
								.getAtmosphericPressure());
			}

			//Component.components.get(0);
			if (accessor.getPlayer().isSneaking()) {
				tile.getComponent().addTooltip(currenttip,
						accessor.getNBTData().getCompoundTag("props"));
			} else {
				currenttip.add(EnumChatFormatting.RED.toString()
						+ EnumChatFormatting.ITALIC.toString()

						+ "Hold <SHIFT> for details");
			}
			//properties
			//NBTTagCompound propData = accessor.getNBTData().getCompoundTag("properties");
			//for (ComponentPropertySet propSet : te.getComponent().propertySets) {
			//	for (ComponentProperty prop : propSet.properties) {
			//		setProperty(prop, propData);
			//	}
			//}
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te,
			NBTTagCompound tag, World world, int x, int y, int z) {
		if (te instanceof TileMachineComponent) {
			tag.setFloat("Temperature",
					((TileMachineComponent) te).getTemperature());
			tag.setFloat("MaxTemperature",
					((TileMachineComponent) te).getMaxTemperature());
			NBTTagCompound n = new NBTTagCompound();
			((TileMachineComponent) te).writeComponentPropertiesToNBT(n);
			tag.setTag("props", n);

			//			tag.setInteger("Component",((TileMachineComponent) te).getComponent())
		}
		if (te instanceof TileMachineComponentTank) {
			tag.setDouble("Pressure", ((TileMachineComponentTank) te).getTank()
					.getTotalPressure());
			tag.setDouble("MaxPressure",
					((TileMachineComponentTank) te).getMaxPressure());
		}
		return tag;
	}

	public static void callbackRegister(IWailaRegistrar reg) {
		reg.registerBodyProvider(new TFTWaila(), TileMachineComponent.class);
		reg.registerNBTProvider(new TFTWaila(), TileMachineComponent.class);
	}

}
