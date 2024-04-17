package lordfokas.cartography.feature.discovery;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.engine.BlazeMapAsync;
import lordfokas.cartography.feature.TFCContent;

public class DiscoveryHandler {
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        if(!level.isClientSide) return;
        BlockPos pos = event.getPos();
        TFCContent.Profile profile = TFCContent.getProfile(level.getBlockState(pos).getBlock());
        if(profile != null && profile.type.classification == TFCContent.Classification.DISCOVERY) {
            switch(profile.type) {
                case NUGGET -> addNugget(level.dimension(), pos, profile.name);
                case FRUIT -> addFruit(level.dimension(), pos, profile.name);
                case CROP -> addCrop(level.dimension(), pos, profile.name);
            }
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(event.getPlayer() != mc.player) return;
        LevelAccessor level = event.getWorld();
        BlockPos pos = event.getPos();
        TFCContent.Profile profile = TFCContent.getProfile(level.getBlockState(pos).getBlock());
        if(profile != null && profile.type.classification == TFCContent.Classification.DISCOVERY) {
            if(profile.type == TFCContent.Type.NUGGET) {
                addNugget(mc.level.dimension(), pos, profile.name);
            }
        }
    }

    public static void removeDiscovery(ResourceKey<Level> dimension, BlockPos pos, TFCContent.Profile discovery) {
        switch(discovery.type) {
            case CROP -> removeCrop(dimension, pos, discovery.name);
            case FRUIT -> removeFruit(dimension, pos, discovery.name);
        }
    }

    private static void addNugget(ResourceKey<Level> dimension, BlockPos pos, String nugget) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getNuggetPool(dimension, nugget).addData(pos, new DiscoveryState(false)));
    }

    private static void addFruit(ResourceKey<Level> dimension, BlockPos pos, String fruit) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getFruitPool(dimension, fruit).addData(pos, new DiscoveryState(false)));
    }

    private static void removeFruit(ResourceKey<Level> dimension, BlockPos pos, String fruit) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getFruitPool(dimension, fruit).removeAll(c -> c.atY(0).equals(pos)));
    }

    private static void addCrop(ResourceKey<Level> dimension, BlockPos pos, String crop) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getCropPool(dimension, crop).addData(pos, new DiscoveryState(false)));
    }

    private static void removeCrop(ResourceKey<Level> dimension, BlockPos pos, String crop) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getCropPool(dimension, crop).removeAll(c -> c.atY(0).equals(pos)));
    }
}
