import sun.misc.Unsafe;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Enum.valueOf;

public class CristalixModLoader {

    private static final String
            MINECRAFT_MAIN_CLASS_NAME = "net.minecraft.client.main.Main",
            MINECRAFT_CLASS_NAME = "LhJdpCu",
            MINECRAFT_INSTANCE_FIELD = "XkTqykZ",
            MINECRAFT_POST_MAIN_THREAD = "gWjrZHr",
            MOD_MANAGER_INSTANCE_FIELD = "ag";
    ;

    private static final String
            MOD_MANAGER_CLASS_NAME = "hzpEdud",
            CLIENT_INSTANCE_FIELD = "kPRmjRd";


    private static final String
            MOD_LOADER_CLASS_NAME = "teWrBzj",
            MOD_LOADER_LOAD_METHOD = "gWjrZHr";

    private static final String MOD_CLASS_NAME = "SWRgULV";

    private static final String
            CLIENT_API_CLASS_NAME = "dev.xdark.clientapi.ClientApi",
            SIDE_CLASS_NAME = "dev.xdark.clientapi.Side";

    public static void inject() throws Throwable {
        System.out.println("Cristalix Mod Injector by dargen (https://github.com/asyncdargen)");
        List<Path> modFiles = Files.list(Paths.get("C:/cristalix-mods"))
                .filter(file -> file.getFileName().toString().endsWith(".jar"))
                .collect(Collectors.toList());

        Object minecraft = getMinecraft();
        Object modManager = getModManager(minecraft);
        Object clientApi = getClientApi(modManager);

        postMainThread(minecraft, () -> {
            for (Path mod : modFiles) {
                try (InputStream is = Files.newInputStream(mod)) {
                    System.out.println("Loading mod " + mod);
                    load(modManager, clientApi, is, null, true);
                } catch (Throwable t) {
                    System.out.println("Error while mod loading " + mod);
                    t.printStackTrace();
                }
            }
        });
    }

    public static Object getMinecraft() throws Throwable {
        return MINECRAFT_INSTANCE_MH.invoke();
    }

    public static Object getModManager(Object minecraft) throws Throwable {
        return MOD_MANAGER_INSTANCE_MH.invoke(minecraft);
    }

    private static Object getClientApi(Object modManager) throws Throwable {
        return CLIENT_API_INSTANCE_MH.invoke(modManager);
    }

    public static Object load(Object modManager, Object clientApi, InputStream inputStream,
                              Object side, boolean flag) throws Throwable {
        return LOAD_MOD_MH.invoke(modManager, clientApi, inputStream, side, flag);
    }

    public static void postMainThread(Object minecraft, Runnable runnable) throws Throwable {
        POST_MAIN_THREAD_MH.invoke(minecraft, runnable);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getSide(String name) throws Throwable {
        return valueOf((Class<Enum>) Class.forName(SIDE_CLASS_NAME), name);
    }

    static {
        try {
            loadAccess();
            loadClasses();
            loadMethodHandles();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static MethodHandle MINECRAFT_INSTANCE_MH;
    private static MethodHandle MOD_MANAGER_INSTANCE_MH;
    private static MethodHandle CLIENT_API_INSTANCE_MH;

    private static MethodHandle POST_MAIN_THREAD_MH;
    private static MethodHandle LOAD_MOD_MH;


    private static void loadMethodHandles() throws Throwable {
        MINECRAFT_INSTANCE_MH = LOOKUP.findStaticGetter(MINECRAFT_CLASS, MINECRAFT_INSTANCE_FIELD, Object.class);
        MOD_MANAGER_INSTANCE_MH = LOOKUP.findGetter(MINECRAFT_CLASS, MOD_MANAGER_INSTANCE_FIELD, Object.class);
        CLIENT_API_INSTANCE_MH = LOOKUP.findGetter(MOD_MANAGER_CLASS, CLIENT_INSTANCE_FIELD, Object.class);

        POST_MAIN_THREAD_MH = LOOKUP.findVirtual(MINECRAFT_CLASS, MINECRAFT_POST_MAIN_THREAD,
                MethodType.methodType(Class.forName("com.google.common.util.concurrent.ListenableFuture"), Runnable.class));
        LOAD_MOD_MH = LOOKUP.findStatic(MOD_LOADER_CLASS, MOD_LOADER_LOAD_METHOD,
                MethodType.methodType(MOD_CLASS, MOD_MANAGER_CLASS, CLIENT_API_CLASS, InputStream.class, SIDE_CLASS, boolean.class));
    }

    private static Class<?> MINECRAFT_CLASS;
    private static Class<?> MOD_MANAGER_CLASS;
    private static Class<?> MOD_LOADER_CLASS;
    private static Class<?> MOD_CLASS;

    private static Class<?> CLIENT_API_CLASS;
    private static Class<?> SIDE_CLASS;

    private static void loadClasses() throws Throwable {
        MINECRAFT_CLASS = Class.forName(MINECRAFT_CLASS_NAME);
        MOD_MANAGER_CLASS = Class.forName(MOD_MANAGER_CLASS_NAME);
        MOD_LOADER_CLASS = Class.forName(MOD_LOADER_CLASS_NAME);
        MOD_CLASS = Class.forName(MOD_CLASS_NAME);

        CLIENT_API_CLASS = Class.forName(CLIENT_API_CLASS_NAME);
        SIDE_CLASS = Class.forName(SIDE_CLASS_NAME);
    }

    private static Unsafe UNSAFE;
    private static MethodHandles.Lookup LOOKUP;

    private static void loadAccess() throws Throwable {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        UNSAFE = (Unsafe) field.get(null);

        Field var1 = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        var1.setAccessible(true);
        LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(UNSAFE.staticFieldBase(var1), UNSAFE.staticFieldOffset(var1));
    }

}
