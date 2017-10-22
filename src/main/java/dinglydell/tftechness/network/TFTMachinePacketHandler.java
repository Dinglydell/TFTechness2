package dinglydell.tftechness.network;

import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class TFTMachinePacketHandler implements
		IMessageHandler<PacketTFTMachine, IMessage> {

	@Override
	public IMessage onMessage(PacketTFTMachine message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;
		message.getTE(world).readClientToServerMessage(message.nbt);
		return null;
	}

}
