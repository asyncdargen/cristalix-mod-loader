package ru.dargen.loader.mapping;

import lombok.Data;

import java.util.Map;

@Data
public class ClassMapping {

    private final String actual;
    private final Map<String, String> fields;
    private final Map<String, String> methods;

    public String mapField(String fieldName) {
        return fields.getOrDefault(fieldName, fieldName);
    }

    public String mapMethod(String methodName, String methodDescriptor) {
        return methods.getOrDefault(methodName + methodDescriptor, methodName);
    }

}
