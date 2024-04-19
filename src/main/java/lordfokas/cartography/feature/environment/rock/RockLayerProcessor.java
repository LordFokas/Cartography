package lordfokas.cartography.feature.environment.rock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.pipeline.PipelineType;
import com.eerussianguy.blazemap.api.pipeline.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.surface.SurfaceMD;
import lordfokas.cartography.feature.environment.ProfileCounter;

public class RockLayerProcessor extends Processor.Direct {
    private static final ThreadLocal<ProfileCounter> COUNTER = ThreadLocal.withInitial(ProfileCounter::new);

    public RockLayerProcessor() {
        super(
            CartographyReferences.Processors.ROCK_LAYER,
            CartographyReferences.MasterData.SURFACE
        );
    }

    @Override
    public boolean shouldExecuteIn(ResourceKey<Level> dimension, PipelineType pipeline) {
        return pipeline.isClient;
    }

    @Override
    public void execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource data) {
        SurfaceMD surface = (SurfaceMD) data.get(CartographyReferences.MasterData.SURFACE);
        ProfileCounter counter = COUNTER.get();
        counter.consume(surface.rock);
        String rock = counter.getDominantName();
        RockClusterStore.getDataPool(dimension, rock).addData(chunk, rock);
    }
}
