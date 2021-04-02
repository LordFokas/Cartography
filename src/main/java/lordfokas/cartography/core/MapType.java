package lordfokas.cartography.core;

import lordfokas.cartography.modules.Module;

public enum MapType {
    ISOHYETAL(Module.METEOROLOGY),
    ISOTHERMAL(Module.METEOROLOGY),
    BIOGEOGRAPHICAL(Module.BIOLOGY),
    GEOLOGICAL(Module.GEOLOGY);

    public final Module module;

    MapType(Module module){
        this.module = module;
    }
}
