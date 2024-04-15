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
    public static void onInteract(PlayerInteractEvent event) {
        Level level = event.getWorld();
        if(!level.isClientSide) return;
        BlockPos pos = event.getPos();
        TFCContent.Profile profile = TFCContent.getProfile(level.getBlockState(pos).getBlock());
        if(profile != null && profile.type.classification == TFCContent.Classification.DISCOVERY) {
            switch(profile.type) {
                case NUGGET -> nugget(level.dimension(), pos, profile.name);
                case FRUIT -> fruit(level.dimension(), pos, profile.name);
                case CROP -> crop(level.dimension(), pos, profile.name);
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
            switch(profile.type) {
                case NUGGET -> nugget(mc.level.dimension(), pos, profile.name);
                case CROP -> crop(mc.level.dimension(), pos, profile.name);
            }
        }
    }

    private static void nugget(ResourceKey<Level> dimension, BlockPos pos, String nugget) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getNuggetPool(dimension, nugget).addData(pos, new DiscoveryState(false)));
    }

    private static void fruit(ResourceKey<Level> dimension, BlockPos pos, String fruit) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getFruitPool(dimension, fruit).addData(pos, new DiscoveryState(false)));
    }

    private static void crop(ResourceKey<Level> dimension, BlockPos pos, String crop) {
        BlazeMapAsync.instance().clientChain.runOnDataThread(() -> DiscoveryClusterStore.getCropPool(dimension, crop).addData(pos, new DiscoveryState(false)));
    }
}
