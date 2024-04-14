package lordfokas.cartography.feature.environment.rock;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.event.ServerJoinedEvent;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.engine.BlazeMapAsync;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.data.ClusterStore;
import lordfokas.cartography.data.IClusterConsumer;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.utils.TFCBlockTypes;

public class RockClusterStore extends ClusterStore {
    private static final HashMap<ResourceKey<Level>, HashMap<String, RockDataPool>> REALMS = new HashMap<>();

    @SubscribeEvent
    public static void onServerJoined(ServerJoinedEvent event) {
        REALMS.clear();
    }

    @SubscribeEvent
    public static void onDimensionChanged(DimensionChangedEvent event) {
        foreach(ClusterType.ROCKS, rock -> BlazeMapAsync.instance().clientChain.runOnDataThread(() -> getDataPool(event.dimension, rock)));
    }

    public static synchronized RockDataPool getDataPool(ResourceKey<Level> dimension, String rock) {
        return REALMS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(rock, $ -> new RockDataPool(
                storage(), getClusterNode(ClusterType.ROCKS, rock),
                new RockClusterRealm(BlazeMapAsync.instance().cruncher.getThreadAsserter(), CONSUMER, rock)
            ));
    }

    private static final IClusterConsumer<RockCluster> CONSUMER = new IClusterConsumer<>() {
        @Override
        public void pushCluster(RockCluster cluster) {
            ResourceKey<Level> dimension = Minecraft.getInstance().level.dimension();
            BlockPos center = cluster.centerOfMass();
            if(center == null) return;
            String rock = cluster.getData();
            ImageHandler.DynamicLabel dynamicLabel = ImageHandler.getLabel(pretty(rock), TFCBlockTypes.getLooseRockTexturePath(rock));
            MapLabel label = new MapLabel(
                clusterID(cluster, "rock"),
                dimension,
                center,
                CartographyReferences.Layers.Fake.ROCKS,
                dynamicLabel.path,
                dynamicLabel.image.getWidth(),
                dynamicLabel.image.getHeight(),
                Colors.NO_TINT,
                0,
                false,
                TFCBlockTypes.getRockTags(rock)
            );
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> {
                var labels = labels();
                if(labels.has(label)) {
                    labels.remove(label);
                }
                labels.add(label);
            });
        }

        @Override
        public void dropCluster(RockCluster cluster) {
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> labels().remove(clusterID(cluster, "rock"), CartographyReferences.Layers.Fake.ROCKS));
        }
    };
}
