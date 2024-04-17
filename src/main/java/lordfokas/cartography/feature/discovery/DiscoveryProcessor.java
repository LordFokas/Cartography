package lordfokas.cartography.feature.discovery;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.pipeline.PipelineType;
import com.eerussianguy.blazemap.api.pipeline.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.TFCContent.Profile;
import lordfokas.cartography.feature.mapping.surface.SurfaceMD;

public class DiscoveryProcessor extends Processor.Differential {

    public DiscoveryProcessor() {
        super(
            CartographyReferences.Processors.DISCOVERY,
            CartographyReferences.MasterData.SURFACE
        );
    }

    @Override
    public void execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource current, IDataSource previous) {
        SurfaceMD previousMD = (SurfaceMD) previous.get(CartographyReferences.MasterData.SURFACE);
        SurfaceMD currentMD = (SurfaceMD) current.get(CartographyReferences.MasterData.SURFACE);
        if(previousMD == null || currentMD == null) return;

        Profile[][] prev = previousMD.discoveries;
        Profile[][] curr = currentMD.discoveries;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                Profile discovery = prev[x][z];
                if(discovery != null && !discovery.equals(curr[x][z])) {
                    DiscoveryHandler.removeDiscovery(dimension, chunk.getBlockAt(x, 0, z), discovery);
                }
            }
        }
    }

    @Override
    public boolean shouldExecuteIn(ResourceKey<Level> dimension, PipelineType pipeline) {
        return pipeline.isClient;
    }
}
