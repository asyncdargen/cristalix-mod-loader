package ru.dargen.loader.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class Access {

    public static Unsafe UNSAFE;
    public static MethodHandles.Lookup LOOKUP;

    static {
        initAccess();
    }

    @SneakyThrows
    private static void initAccess() {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        UNSAFE = (Unsafe) unsafeField.get(null);

        Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        lookupField.setAccessible(true);
        LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(UNSAFE.staticFieldBase(lookupField), UNSAFE.staticFieldOffset(lookupField));
    }

}
