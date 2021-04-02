package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.modules.Module;

public class Biology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.BIOLOGY);

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
    }
}
