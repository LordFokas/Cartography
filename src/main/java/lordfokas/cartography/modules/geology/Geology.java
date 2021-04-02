package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.modules.Module;

public class Geology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.GEOLOGY);

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
    }
}
