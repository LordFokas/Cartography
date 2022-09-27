package lordfokas.cartography.feature.discovery;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.engine.BlazeMapEngine;
import lordfokas.cartography.utils.TFCBlockTypes;

public class DiscoveryHandler {
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent event) {
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        TFCBlockTypes.Profile profile = TFCBlockTypes.getProfile(level.getBlockState(pos).getBlock());
        if(profile != null && profile.type.classification == TFCBlockTypes.Classification.DISCOVERY) {
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
        TFCBlockTypes.Profile profile = TFCBlockTypes.getProfile(level.getBlockState(pos).getBlock());
        if(profile != null && profile.type.classification == TFCBlockTypes.Classification.DISCOVERY) {
            switch(profile.type) {
                case NUGGET -> nugget(mc.level.dimension(), pos, profile.name);
                case CROP -> crop(mc.level.dimension(), pos, profile.name);
            }
        }
    }

    private static void nugget(ResourceKey<Level> dimension, BlockPos pos, String nugget) {
        BlazeMapEngine.async().runOnDataThread(() -> DiscoveryClusterStore.getNuggetPool(dimension, nugget).addData(pos, nugget));
    }

    private static void fruit(ResourceKey<Level> dimension, BlockPos pos, String fruit) {
        BlazeMapEngine.async().runOnDataThread(() -> DiscoveryClusterStore.getFruitPool(dimension, fruit).addData(pos, fruit));
    }

    private static void crop(ResourceKey<Level> dimension, BlockPos pos, String crop) {
        BlazeMapEngine.async().runOnDataThread(() -> DiscoveryClusterStore.getCropPool(dimension, crop).addData(pos, crop));
    }
}
