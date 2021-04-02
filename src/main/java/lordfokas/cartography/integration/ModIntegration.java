package lordfokas.cartography.integration;

import lordfokas.cartography.Cartography;
import lordfokas.cartography.integration.journeymap.JMIntegration;
import lordfokas.cartography.integration.terrafirmacraft.TFCIntegration;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public enum ModIntegration {
    TFC("tfc", TFCIntegration::loadMapTypes, null),
    TFCEXTENDED("tfcextended", null, null),
    JOURNEYMAP("journeymap", null, JMIntegration::createMaps);

    private final String modId;
    private final IIntegrationLoader pre, post;

    ModIntegration(String modId, IIntegrationLoader pre, IIntegrationLoader post){
        this.modId = modId;
        this.pre = pre;
        this.post = post;
    }

    public boolean isLoaded(){
        return FMLLoader.getLoadingModList().getModFileById(this.modId) != null;
    }

    public boolean isEnabled(){ // TODO: have configs disable this
        return true;
    }

    public void load(LoadPhase phase){
        Logger logger = Cartography.logger();
        if(!isLoaded()){
            logger.warn("Skipping integration phase {} with {} - Mod not loaded", phase.name(), this.name());
        }else if(!isEnabled()){
            logger.warn("Skipping integration phase {} with {} - Disabled in config", phase.name(), this.name());
        }else{
            IIntegrationLoader loader = phase.getLoader(this);
            if(loader == null){
                logger.info("Skipping integration phase {} with {} - NOOP", phase.name(), this.name());
            }else{
                logger.info("Beginning integration phase {} with {}", phase.name(), this.name());
                loader.run();
                logger.info("Finished integration phase {} with {}", phase.name(), this.name());
            }
        }
    }

    @FunctionalInterface
    public interface IIntegrationLoader{
        void run();
    }

    public enum LoadPhase {
        PRE(m -> m.pre),
        POST(m -> m.post);

        private final Function<ModIntegration, IIntegrationLoader> fn;

        LoadPhase(Function<ModIntegration, IIntegrationLoader> fn){
            this.fn = fn;
        }

        public IIntegrationLoader getLoader(ModIntegration mod){
            return fn.apply(mod);
        }
    }
}
