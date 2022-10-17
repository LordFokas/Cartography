package lordfokas.cartography.feature.environment.rock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.pipeline.PipelineType;
import com.eerussianguy.blazemap.api.pipeline.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.ground.GroundCompositionMD;
import lordfokas.cartography.utils.ProfileCounter;

public class RockLayerProcessor extends Processor {
    private static final ThreadLocal<ProfileCounter> COUNTER = ThreadLocal.withInitial(ProfileCounter::new);

    public RockLayerProcessor() {
        super(
            CartographyReferences.Processors.ROCK_LAYER,
            CartographyReferences.MasterData.GROUND_COMPOSITION
        );
    }

    @Override
    public boolean shouldExecuteIn(ResourceKey<Level> dimension, PipelineType pipeline) {
        return pipeline.isClient;
    }

    @Override
    public boolean execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource data) {
        GroundCompositionMD ground = (GroundCompositionMD) data.get(CartographyReferences.MasterData.GROUND_COMPOSITION);
        ProfileCounter counter = COUNTER.get();
        counter.consume(ground.rock);
        String rock = counter.getDominantName();
        RockClusterStore.getDataPool(dimension, rock).addData(chunk, rock);
        return false;
    }
}
