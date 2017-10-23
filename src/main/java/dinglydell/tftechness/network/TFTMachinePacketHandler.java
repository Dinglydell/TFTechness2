package dinglydell.tftechness.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class TFTMachinePacketHandler implements
		IMessageHandler<PacketTFTMachine, IMessage> {

	@Override
	public IMessage onMessage(PacketTFTMachine message, MessageContext ctx) {
		World world;
		if (ctx.side == Side.CLIENT) {
			world = Minecraft.getMinecraft().theWorld;
			message.getTE(world).readServerToClientMessage(message.nbt);
		} else {
			world = ctx.getServerHandler().playerEntity.worldObj;
			message.getTE(world).readClientToServerMessage(message.nbt);
		}

		return null;
	}

}
