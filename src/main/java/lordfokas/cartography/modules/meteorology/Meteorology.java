package lordfokas.cartography.modules.meteorology;

import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.modules.Module;

public class Meteorology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.METEOROLOGY);

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
    }
}
