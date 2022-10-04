package lordfokas.cartography.feature.mapping.ground;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import com.eerussianguy.blazemap.api.pipeline.Collector;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.TFCBlockTypes;
import lordfokas.cartography.utils.TFCBlockTypes.Classification;
import lordfokas.cartography.utils.TFCBlockTypes.Profile;

public class GroundCompositionCollector extends Collector<GroundCompositionMD> {

    public GroundCompositionCollector() {
        super(CartographyReferences.Collectors.GROUND_COMPOSITION, CartographyReferences.MasterData.GROUND_COMPOSITION);
    }

    @Override
    public GroundCompositionMD collect(Level level, int minX, int minZ, int maxX, int maxZ) {
        Profile[][] soils = new Profile[16][16];
        Profile[][] rocks = new Profile[16][16];

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, minX + x, minZ + z);

                boolean soil = false;
                while(y > level.getMinBuildHeight()) {
                    BlockState state = level.getBlockState(POS.set(minX + x, y, minZ + z));
                    Profile profile = TFCBlockTypes.getProfile(state.getBlock(), Classification.ROCK, Classification.SOIL);
                    if(profile != null) {
                        if(!soil) {
                            if(profile.type.classification == Classification.SOIL) {
                                soils[x][z] = profile;
                                soil = true;
                                continue;
                            }
                        }
                        if(profile.type.classification == Classification.ROCK) {
                            rocks[x][z] = profile;
                            break;
                        }
                    }
                    y--;
                }
            }
        }

        return new GroundCompositionMD(soils, rocks);
    }
}
