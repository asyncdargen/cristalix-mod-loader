package ru.dargen.loader.transformer;

import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import ru.dargen.loader.mapping.Mappings;

import java.util.List;

@RequiredArgsConstructor
public class ClassTransformer {

    private final Mappings mappings;
    private final InstructionTransformer instructionTransformer;

    public void transform(ClassNode classNode) {
        transformClass(classNode);
        transformFields(classNode.fields);
        transformMethods(classNode.methods);
    }

    private void transformClass(ClassNode classNode) {
        classNode.superName = mappings.mapClassName(classNode.superName);
        classNode.interfaces = mappings.mapClassesNames(classNode.interfaces);
        if (classNode.signature != null) {
            classNode.signature = mappings.mapClassNames(classNode.signature);
        }
        if (classNode.outerMethodDesc != null) {
            classNode.outerMethodDesc = mappings.mapClassNames(classNode.outerMethodDesc);
        }
    }

    private void transformFields(List<FieldNode> fields) {
        fields.forEach(field -> field.desc = mappings.mapClassNames(field.desc));
    }

    private void transformMethods(List<MethodNode> methods) {
        methods.forEach(this::transformMethod);
    }

    private void transformMethod(MethodNode method) {
        method.desc = mappings.mapClassNames(method.desc);
        if (method.signature != null) {
            method.signature = mappings.mapClassNames(method.signature);
        }
        if (method.exceptions != null) {
            method.exceptions = mappings.mapClassesNames(method.exceptions);
        }
        if (method.localVariables != null) {
            method.localVariables.forEach(var -> {
                var.desc = mappings.mapClassNames(var.desc);
                if (var.signature!= null) {
                    var.signature = mappings.mapClassNames(var.signature);
                }
            });
        }

        instructionTransformer.transformList(method.instructions);
    }

}
