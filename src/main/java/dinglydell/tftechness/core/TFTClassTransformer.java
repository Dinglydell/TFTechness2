package dinglydell.tftechness.core;

import java.util.ArrayList;
import java.util.List;

import jdk.internal.org.objectweb.asm.Type;
import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import com.bioxx.tfc.ASM.ClassTransformer;

public class TFTClassTransformer extends ClassTransformer {

	public TFTClassTransformer() {
		mcpClassName = "com.bioxx.tfc.Core.TFC_Climate";
		obfClassName = "com.bioxx.tfc.Core.TFC_Climate";

		List<InstrSet> nodes = new ArrayList<InstrSet>();
		InsnList list = new InsnList();
		//TFC_Climate.;
		//EntityLeashKnot
		String desc = Type.getMethodDescriptor(Type.getType(World.class),
				Type.INT_TYPE,
				Type.INT_TYPE,
				Type.INT_TYPE,
				Type.INT_TYPE,
				Type.BOOLEAN_TYPE);
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				"dinglydell/tftechness/world/TFT_Climate", "getTemp0", desc));

		nodes.add(new InstrSet(list, 0, InstrOpType.Replace));

		mcpMethodNodes.put("getTemp0 | " + desc, new Patch(nodes,
				PatchOpType.Replace));

	}

	//@Override
	//public byte[] transform(String name, String transformedName,
	//		byte[] bytes) {
	//	if(name.equals("TFC_Climate")){
	//		TFTechness2.logger.info("Hacking TFC_Climate...");
	//		String targetMethodName = "getTemp0";
	//		ClassNode classNode = new ClassNode();
	//		ClassReader classReader = new ClassReader(bytes);
	//		classReader.accept(classNode, 0);
	//		net.minecraft.client.renderer.RenderBlocks
	//	}
	//	return null;
	//}

}
