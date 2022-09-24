package lordfokas.cartography;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.eerussianguy.blazemap.api.event.BlazeRegistryEvent.*;
import com.mojang.logging.LogUtils;
import lordfokas.cartography.feature.data.*;
import lordfokas.cartography.feature.mapping.*;
import lordfokas.cartography.utils.ImageHandler;
import org.slf4j.Logger;

@Mod(CartographyReferences.MOD_ID)
public class Cartography {
    public static final Logger LOGGER = LogUtils.getLogger();

    public Cartography() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ImageHandler.init();

        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ClimateClusterStore::onDimensionChanged);
        bus.addListener(RockClusterStore::onDimensionChanged);
    }

    @SubscribeEvent
    public void registerCollectors(CollectorRegistryEvent evt){
        evt.registry.register(new ClimateCollector());
        evt.registry.register(new GroundCompositionCollector());
    }

    @SubscribeEvent
    public void registerLayers(LayerRegistryEvent evt){
        evt.registry.register(new RainfallLayer());
        evt.registry.register(new RainfallIsolinesLayer());
        evt.registry.register(new TemperatureLayer());
        evt.registry.register(new TemperatureIsolinesLayer());
        evt.registry.register(new EcosystemLayer());
        evt.registry.register(new GeologyLayer());
    }

    @SubscribeEvent
    public void registerMapTypes(MapTypeRegistryEvent evt){
        evt.registry.register(new RainfallMapType());
        evt.registry.register(new TemperatureMapType());
        evt.registry.register(new EcosystemMapType());
        evt.registry.register(new GeologyMapType());
    }

    @SubscribeEvent
    public void registerProcessors(ProcessorRegistryEvent evt){
        evt.registry.register(new ClimateProcessor());
        evt.registry.register(new RockLayerProcessor());
    }

    public static TranslatableComponent lang(String key) {
        return new TranslatableComponent(CartographyReferences.MOD_ID + "." + key);
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(CartographyReferences.MOD_ID, path);
    }
}
