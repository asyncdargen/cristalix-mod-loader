package ru.dargen.loader.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

@UtilityClass
public class Asm {

    public ClassNode readClass(byte[] bytecode) {
        val reader = new ClassReader(bytecode);
        val classNode = new ClassNode();
        reader.accept(classNode, 0);

        return classNode;
    }

    public byte[] toByteCode(ClassNode classNode) {
        val writer = new ClassWriter(0);
        classNode.accept(writer);

        return writer.toByteArray();
    }


}
