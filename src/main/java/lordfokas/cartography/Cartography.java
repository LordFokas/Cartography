package lordfokas.cartography;

import lordfokas.cartography.integration.journeymap.JMHacks;
import lordfokas.cartography.modules.biology.Biology;
import lordfokas.cartography.modules.geology.Geology;
import lordfokas.cartography.modules.meteorology.Meteorology;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("cartography")
public class Cartography {
    private static final Logger LOGGER = LogManager.getLogger();

    public Cartography(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event){
        JMHacks.init();

        Meteorology.init();
        Biology.init();
        Geology.init();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event){ }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent){ }
    }
}
