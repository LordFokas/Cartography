package lordfokas.cartography.modules;

import lordfokas.cartography.Cartography;
import lordfokas.cartography.modules.biology.Biology;
import lordfokas.cartography.modules.geology.Geology;
import lordfokas.cartography.modules.meteorology.Meteorology;

public enum Module {
    BIOLOGY(Biology::init),
    GEOLOGY(Geology::init),
    METEOROLOGY(Meteorology::init);

    private final IModuleInitializer initializer;

    Module(IModuleInitializer initializer){
        this.initializer = initializer;
    }

    public void init(){
        Cartography.logger().info("Initializing module {}", this.name());
        this.initializer.init();
        Cartography.logger().info("Module {} initialized", this.name());
    }

    @FunctionalInterface
    interface IModuleInitializer{
        void init();
    }
}
