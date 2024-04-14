package lordfokas.cartography;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.eerussianguy.blazemap.api.debug.ModAnnouncementEvent;
import com.eerussianguy.blazemap.api.event.BlazeRegistryEvent.*;
import com.eerussianguy.blazemap.api.maps.FakeLayer;
import com.mojang.logging.LogUtils;
import lordfokas.cartography.data.ClusterStore;
import lordfokas.cartography.data.SerializableDataPool;
import lordfokas.cartography.feature.discovery.DiscoveryClusterStore;
import lordfokas.cartography.feature.discovery.DiscoveryHandler;
import lordfokas.cartography.feature.discovery.DiscoveryMapMenu;
import lordfokas.cartography.feature.discovery.DiscoveryMarkerRenderer;
import lordfokas.cartography.feature.environment.climate.ClimateClusterStore;
import lordfokas.cartography.feature.environment.climate.ClimateProcessor;
import lordfokas.cartography.feature.environment.rock.RockClusterStore;
import lordfokas.cartography.feature.environment.rock.RockLayerProcessor;
import lordfokas.cartography.feature.mapping.climate.*;
import lordfokas.cartography.feature.mapping.ground.*;
import lordfokas.cartography.utils.ImageHandler;
import org.slf4j.Logger;

@Mod(CartographyReferences.MOD_ID)
public class Cartography {
    public static final Logger LOGGER = LogUtils.getLogger();

    public Cartography() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Initialize generic utility mechanisms
        MinecraftForge.EVENT_BUS.register(SerializableDataPool.class);
        MinecraftForge.EVENT_BUS.register(ClusterStore.class);

        // Register self for Blaze Map features
        MinecraftForge.EVENT_BUS.register(this);

        // Initialize specific feature facilities
        MinecraftForge.EVENT_BUS.register(ClimateClusterStore.class);
        MinecraftForge.EVENT_BUS.register(RockClusterStore.class);
        MinecraftForge.EVENT_BUS.register(DiscoveryHandler.class);
        MinecraftForge.EVENT_BUS.register(DiscoveryClusterStore.class);
        MinecraftForge.EVENT_BUS.register(DiscoveryMapMenu.class);
    }

    @SubscribeEvent
    public void registerMasterData(MasterDataRegistryEvent evt) {
        evt.registry.register(new ClimateSerializer());
        evt.registry.register(new ClimateIsolinesSerializer());
        evt.registry.register(new GroundCompositionSerializer());
    }

    @SubscribeEvent
    public void registerCollectors(CollectorRegistryEvent evt) {
        evt.registry.register(new ClimateCollector());
        evt.registry.register(new GroundCompositionCollector());
    }

    @SubscribeEvent
    public void registerTransformers(TransformerRegistryEvent evt) {
        evt.registry.register(new ClimateIsolinesTransformer());
    }

    @SubscribeEvent
    public void registerProcessors(ProcessorRegistryEvent evt) {
        evt.registry.register(new ClimateProcessor());
        evt.registry.register(new RockLayerProcessor());
    }

    @SubscribeEvent
    public void registerLayers(LayerRegistryEvent evt) {
        ImageHandler.init();

        evt.registry.register(new RainfallLayer());
        evt.registry.register(new RainfallIsolinesLayer());

        evt.registry.register(new TemperatureLayer());
        evt.registry.register(new TemperatureIsolinesLayer());

        evt.registry.register(new EcosystemLayer());
        evt.registry.register(new FakeLayer(CartographyReferences.Layers.Fake.CROPS, lang("layer.ecosystem.crops"), resource("icons/layers/crops.png")));
        evt.registry.register(new FakeLayer(CartographyReferences.Layers.Fake.FRUIT, lang("layer.ecosystem.fruit"), resource("icons/layers/fruit.png")));
        evt.registry.register(new FakeLayer(CartographyReferences.Layers.Fake.TREES, lang("layer.ecosystem.trees"), resource("icons/layers/trees.png")));

        evt.registry.register(new GeologyLayer());
        evt.registry.register(new FakeLayer(CartographyReferences.Layers.Fake.ROCKS, lang("layer.geology.rocks"), resource("icons/layers/rocks.png")));
        evt.registry.register(new FakeLayer(CartographyReferences.Layers.Fake.ORES, lang("layer.geology.ores"), resource("icons/layers/ores.png")));
    }

    @SubscribeEvent
    public void registerMapTypes(MapTypeRegistryEvent evt) {
        evt.registry.register(new RainfallMapType());
        evt.registry.register(new TemperatureMapType());
        evt.registry.register(new EcosystemMapType());
        evt.registry.register(new GeologyMapType());
    }

    @SubscribeEvent
    public void registerObjectRenderers(ObjectRendererRegistryEvent evt) {
        evt.registry.register(new DiscoveryMarkerRenderer());
    }

    @SubscribeEvent
    public void announce(ModAnnouncementEvent evt) {
        evt.announce("tfc");
    }

    public static TranslatableComponent lang(String key) {
        return new TranslatableComponent(CartographyReferences.MOD_ID + "." + key);
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(CartographyReferences.MOD_ID, path);
    }
}
