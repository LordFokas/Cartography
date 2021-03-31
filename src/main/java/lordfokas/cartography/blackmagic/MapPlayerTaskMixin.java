package lordfokas.cartography.blackmagic;

import journeymap.client.cartography.ChunkRenderController;
import journeymap.client.feature.Feature;
import journeymap.client.feature.FeatureManager;
import journeymap.client.model.EntityDTO;
import journeymap.client.model.MapType;
import journeymap.client.task.multi.ITask;
import journeymap.client.task.multi.MapPlayerTask;
import journeymap.common.Journeymap;
import lordfokas.cartography.integration.journeymap.JMHacks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(value = MapPlayerTask.class, remap = false)
public class MapPlayerTaskMixin {

    @Invoker("<init>")
    static MapPlayerTask newMapPlayerTask(ChunkRenderController chunkRenderController, World world, MapType mapType, Collection<ChunkPos> chunkCoords) {
        throw new AssertionError();
    }

    /** @author LordFokas */ @Overwrite
    public static MapPlayerTask.MapPlayerTaskBatch create(ChunkRenderController chunkRenderController, EntityDTO player) {
        boolean surfaceAllowed = FeatureManager.getInstance().isAllowed(Feature.MapSurface);
        boolean cavesAllowed = FeatureManager.getInstance().isAllowed(Feature.MapCaves);
        boolean topoAllowed = FeatureManager.getInstance().isAllowed(Feature.MapTopo);
        if (!surfaceAllowed && !cavesAllowed && !topoAllowed) {
            return null;
        } else {
            LivingEntity playerEntity = player.entityLivingRef.get();
            if (playerEntity == null) {
                return null;
            } else {
                boolean underground = player.underground;
                MapType mapType;
                if (underground) {
                    mapType = MapType.underground(player);
                } else {
                    long time = playerEntity.level.getLevelData().getDayTime() % 24000L;
                    mapType = time < 13800L ? MapType.day(player) : MapType.night(player);
                }

                List<ITask> tasks = new ArrayList<>(2);
                tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, mapType, new ArrayList<>()));
                if (underground) {
                    if (surfaceAllowed && Journeymap.getClient().getCoreProperties().alwaysMapSurface.get()) {
                        tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, MapType.day(player), new ArrayList<>()));
                    }
                } else if (cavesAllowed && Journeymap.getClient().getCoreProperties().alwaysMapCaves.get()) {
                    tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, MapType.underground(player), new ArrayList<>()));
                }

                if (topoAllowed && Journeymap.getClient().getCoreProperties().mapTopography.get()) {
                    tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, MapType.topo(player), new ArrayList<>()));
                }

                tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, MapType.from(JMHacks.ISOHYETAL, null, player.dimension), new ArrayList<>()));
                tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, MapType.from(JMHacks.ISOTHERMAL, null, player.dimension), new ArrayList<>()));
                tasks.add(newMapPlayerTask(chunkRenderController, playerEntity.level, MapType.from(JMHacks.GEOLOGICAL, null, player.dimension), new ArrayList<>()));

                return new MapPlayerTask.MapPlayerTaskBatch(tasks);
            }
        }
    }
}
