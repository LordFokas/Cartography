package lordfokas.cartography.feature.environment.rock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.mapping.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.ground.GroundCompositionMD;
import lordfokas.cartography.utils.StringCounter;

public class RockLayerProcessor extends Processor {
    private static final ThreadLocal<StringCounter> COUNTER = ThreadLocal.withInitial(StringCounter::new);

    public RockLayerProcessor() {
        super(
            CartographyReferences.Processors.ROCK_LAYER,
            CartographyReferences.Collectors.GROUND_COMPOSITION
        );
    }

    @Override
    public boolean execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource data) {
        GroundCompositionMD ground = (GroundCompositionMD) data.get(CartographyReferences.Collectors.GROUND_COMPOSITION);
        StringCounter counter = COUNTER.get();
        counter.consume(ground.rock);
        String rock = counter.getDominant();
        RockClusterStore.getDataPool(dimension, rock).addData(chunk, rock);
        return false;
    }
}
