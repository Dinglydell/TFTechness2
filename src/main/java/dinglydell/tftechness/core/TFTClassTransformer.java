package dinglydell.tftechness.core;

import java.util.ArrayList;
import java.util.List;

import jdk.internal.org.objectweb.asm.Type;
import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.bioxx.tfc.ASM.ClassTransformer;

//import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

public class TFTClassTransformer extends ClassTransformer {

	public TFTClassTransformer() {
		mcpClassName = "com.bioxx.tfc.Core.TFC_Climate";
		obfClassName = "com.bioxx.tfc.Core.TFC_Climate";

		List<InstrSet> nodes = new ArrayList<InstrSet>();
		InsnList list = new InsnList();
		//TFC_Climate.;
		//EntityLeashKnot

		String desc = Type.getMethodDescriptor(Type.FLOAT_TYPE,
				Type.getType(World.class),
				Type.INT_TYPE,
				Type.INT_TYPE,
				Type.INT_TYPE,
				Type.INT_TYPE,
				Type.BOOLEAN_TYPE);
		//new MethodIns
		LabelNode l0 = new LabelNode();
		list.add(l0);
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ILOAD, 1));
		list.add(new VarInsnNode(Opcodes.ILOAD, 2));
		list.add(new VarInsnNode(Opcodes.ILOAD, 3));
		list.add(new VarInsnNode(Opcodes.ILOAD, 4));
		list.add(new VarInsnNode(Opcodes.ILOAD, 5));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				"dinglydell/tftechness/world/TFT_Climate", "getTemp0", desc,
				false));
		list.add(new InsnNode(Opcodes.FRETURN));
		//for (int i = 0; i < 30; i++) { //lots o labels to deal with local variable issue
		//	list.add(new LabelNode());
		//}

		nodes.add(new InstrSet(list, 0, InstrOpType.Replace));

		mcpMethodNodes.put("getTemp0 | " + desc, new Patch(nodes,
				PatchOpType.Modify));

	}

	public byte[] transform(String name, String transformedName, byte[] bytes) {
		return super.transform(name, transformedName, bytes);
	}
	//	@Override
	//	public byte[] transform(String name, String transformedName, byte[] bytes) {
	//		if (name.equals("TFC_Climate")) {
	//			TFTechness2.logger.info("Hacking TFC_Climate...");
	//			String targetMethodName = "getTemp0";
	//			ClassNode classNode = new ClassNode();
	//			ClassReader classReader = new ClassReader(bytes);
	//			classReader.accept(classNode, 0);
	//			//net.minecraft.client.renderer.RenderBlocks
	//
	//Iterator<MethodNode> methods = classNode.methods.iterator();
	//while(methods.hasNext())
	//{
	//}
	//}
	//		}
	//		return bytes;
	//	}

}
