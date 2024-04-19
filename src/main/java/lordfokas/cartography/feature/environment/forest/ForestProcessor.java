package lordfokas.cartography.feature.environment.forest;

import java.util.Objects;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.pipeline.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.TFCContent.Profile;
import lordfokas.cartography.feature.mapping.surface.SurfaceMD;

public class ForestProcessor extends Processor.Differential {

    public ForestProcessor() {
        super(
            CartographyReferences.Processors.FOREST,
            CartographyReferences.MasterData.SURFACE
        );
    }

    @Override
    public void execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource current, IDataSource previous) {
        SurfaceMD previousMD = (SurfaceMD) previous.get(CartographyReferences.MasterData.SURFACE);
        SurfaceMD currentMD = (SurfaceMD) current.get(CartographyReferences.MasterData.SURFACE);

        ForestStore.getStore(dimension, region).change(batch -> {
            for(int x = 0; x < 16; x++) {
                for(int z = 0; z < 16; z++) {
                    Profile prev = previousMD == null ? null : previousMD.tree[x][z];
                    Profile curr = currentMD == null ? null : currentMD.tree[x][z];
                    if(Objects.equals(prev, curr)) {
                        continue;
                    }
                    if(prev != null) {
                        batch.remove(prev.name);
                    }
                    if(curr != null) {
                        batch.add(curr.name);
                    }
                }
            }
        });
    }
}
