package lordfokas.cartography;

import lordfokas.cartography.core.GameContainerClient;
import lordfokas.cartography.core.GameContainerServer;
import lordfokas.cartography.core.ImageHandler;
import lordfokas.cartography.integration.ModIntegration;
import lordfokas.cartography.modules.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Cartography.MOD_ID)
public class Cartography {
    public static final String MOD_ID = "cartography";

    private static final Logger LOGGER = LogManager.getLogger();
    public static Logger logger(){ return LOGGER; }

    public Cartography(){
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new GameContainerClient.EventHandler());
        MinecraftForge.EVENT_BUS.register(new GameContainerServer.EventHandler());
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent evt){
        ImageHandler.init();

        for(ModIntegration integration : ModIntegration.values()){
            integration.load(ModIntegration.LoadPhase.PRE);
        }
        for(Module module : Module.values()){
            module.init();
        }
        for(ModIntegration integration : ModIntegration.values()){
            integration.load(ModIntegration.LoadPhase.POST);
        }
    }
}
