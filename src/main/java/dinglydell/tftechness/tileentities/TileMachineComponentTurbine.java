package dinglydell.tftechness.tileentities;

import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityDynamo;
import dinglydell.tftechness.TFTechness2;
import dinglydell.tftechness.block.component.property.ComponentProperty;
import dinglydell.tftechness.fluid.SolutionTank;
import dinglydell.tftechness.gui.TFTGuiHandler.TFTGuis;

public class TileMachineComponentTurbine extends TileMachineComponentTank {

	protected static float rps = 0;

	public TileMachineComponentTurbine() {
		super();
		tank = new SolutionTank(this, 500);
	}

	@Override
	protected int getCapacity() {

		return 500;
	}

	@Override
	public void writeServerToClientMessage(NBTTagCompound nbt) {
		super.writeServerToClientMessage(nbt);
		nbt.setFloat("RPS", rps);
	}

	@Override
	public void readServerToClientMessage(NBTTagCompound nbt) {
		super.readServerToClientMessage(nbt);
		nbt.setFloat("RPS", rps);
	}

	@Override
	public void initialiseComponent() {
		super.initialiseComponent();
		rps = 0;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		float mass = tank.getContentMass();
		if (mass > 0) {
			rps *= 1 / (0.05f * mass + 1);
		}
		if (rps > getMaxTurbineSpeed()) {
			TFTechness2.logger.info("Overclock! " + rps);
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
		for (Entry<ForgeDirection, Double> entry : dP.entrySet()) {
			ForgeDirection dir = entry.getKey();
			if (dir.ordinal() == blockMetadata
					|| dir.getOpposite().ordinal() == blockMetadata) {
				float dRPS = Math.abs((float) (entry.getValue() * 0.0005f));
				rps += dRPS;
				temperature -= dRPS * 0.01f;
				TileEntity te = getAdjacentTile(dir);
				if (te instanceof TileMachineComponentTurbine) {
					TileMachineComponentTurbine turb = (TileMachineComponentTurbine) te;
					float diff = (turb.rps - rps) * 0.01f;
					rps += diff;
					turb.rps -= diff;
				} else if (te instanceof TileEntityDynamo) {
					TileEntityDynamo dyn = (TileEntityDynamo) te;
					dyn.inputRotation(rps, dir.ordinal());
					rps *= 0.8f;
				}
				//Map<Gas, GasStack> gases;
				//float vol;
				//if (te instanceof TileMachineComponentTank) {
				//	gases = ((TileMachineComponentTank) te).getTank()
				//			.getGasContent();
				//	vol = ((TileMachineComponentTank) te).getTank()
				//			.getGasVolume();
				//} else {
				//	gases = TFTWorldData.get(worldObj)
				//			.getAtmosphericComposition(xCoord + dir.offsetX,
				//					yCoord + dir.offsetY,
				//					zCoord + dir.offsetZ);
				//	vol = 1;
				//}
				//double mass = 0;
				//for (GasStack gas : gases.values()) {
				//	mass += gas.getMass();
				//}
				//double dens = mass / vol;
				//

			}
		}

	}

	public float getMaxTurbineSpeed() {

		return (Float) materials.get(ComponentProperty.MAX_TURBINE_SPEED).validFor
				.get(ComponentProperty.MAX_TURBINE_SPEED);
	}

	public float getRPS() {
		return rps;
	}

	private TileEntity getAdjacentTile(ForgeDirection dir) {

		return worldObj.getTileEntity(xCoord + dir.offsetX, yCoord
				+ dir.offsetY, zCoord + dir.offsetZ);
	}

	@Override
	public boolean openGui(World world, EntityPlayer player) {
		player.openGui(TFTechness2.instance,
				TFTGuis.Turbine.ordinal(),
				world,
				xCoord,
				yCoord,
				zCoord);
		return true;
	}

	@Override
	public IIcon getIcon(int side) {
		ForgeDirection dir = ForgeDirection.values()[side];
		return component.getIcon((isSealed ? 0 : 1)
				+ (((dir.ordinal() == blockMetadata || dir.getOpposite()
						.ordinal() == blockMetadata) ? 1 : 0) << 1));
	}
}
