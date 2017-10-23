package dinglydell.tftechness.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import dinglydell.tftechness.tileentities.TileTFTMachineBase;

public class PacketTFTMachine implements IMessage {
	public NBTTagCompound nbt;

	public PacketTFTMachine() {

	}

	public PacketTFTMachine(TileTFTMachineBase tile, Side side) {
		nbt = new NBTTagCompound();
		if (side == Side.CLIENT) {
			tile.writeClientToServerMessage(nbt);
		} else {
			tile.writeServerToClientMessage(nbt);
		}
		nbt.setInteger("xCoord", tile.xCoord);
		nbt.setShort("yCoord", (short) tile.yCoord);
		nbt.setInteger("zCoord", tile.zCoord);

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);

	}

	public TileTFTMachineBase getTE(World world) {
		return (TileTFTMachineBase) world.getTileEntity(nbt
				.getInteger("xCoord"), nbt.getShort("yCoord"), nbt
				.getInteger("zCoord"));

	}

}
