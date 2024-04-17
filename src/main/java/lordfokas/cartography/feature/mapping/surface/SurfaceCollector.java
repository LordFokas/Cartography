package lordfokas.cartography.feature.mapping.surface;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import com.eerussianguy.blazemap.api.pipeline.Collector;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.TFCContent;
import lordfokas.cartography.feature.TFCContent.Classification;
import lordfokas.cartography.feature.TFCContent.Profile;

public class SurfaceCollector extends Collector<SurfaceMD> {

    public SurfaceCollector() {
        super(CartographyReferences.Collectors.SURFACE, CartographyReferences.MasterData.SURFACE);
    }

    @Override
    public SurfaceMD collect(Level level, int minX, int minZ, int maxX, int maxZ) {
        Profile[][] soils = new Profile[16][16];
        Profile[][] rocks = new Profile[16][16];
        Profile[][] discoveries = new Profile[16][16];

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, minX + x, minZ + z);

                boolean soil = false;
                Profile discovery = null;

                do {
                    BlockState state = level.getBlockState(POS.set(minX + x, y, minZ + z));
                    Profile profile = TFCContent.getProfile(state.getBlock(), Classification.ROCK, Classification.SOIL, Classification.DISCOVERY);
                    if(profile == null) continue;

                    if(profile.type.classification == Classification.DISCOVERY) {
                        discovery = profile;
                        continue;
                    }
                    if(!soil && profile.type.classification == Classification.SOIL) {
                        soils[x][z] = profile;
                        soil = true;
                        if(discovery != null) {
                            discoveries[x][z] = discovery;
                            discovery = null;
                        }
                        continue;
                    }
                    if(profile.type.classification == Classification.ROCK) {
                        rocks[x][z] = profile;
                        if(discovery != null) {
                            discoveries[x][z] = discovery;
                        }
                        break;
                    }
                } while (--y > level.getMinBuildHeight());
            }
        }

        return new SurfaceMD(soils, rocks, discoveries);
    }
}
