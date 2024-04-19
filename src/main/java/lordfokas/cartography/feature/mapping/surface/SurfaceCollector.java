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
        Profile[][] tree = new Profile[16][16];
        Profile[][] find = new Profile[16][16];
        Profile[][] soil = new Profile[16][16];
        Profile[][] rock = new Profile[16][16];

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, minX + x, minZ + z);

                boolean hasSoil = false, hasTree = false;
                Profile discovery = null, treeType = null;
                int trunkHeight = 0, trunkY = 0;

                do {
                    BlockState state = level.getBlockState(POS.set(minX + x, y, minZ + z));
                    Profile profile = TFCContent.getProfile(state.getBlock(), Classification.ROCK, Classification.SOIL, Classification.DISCOVERY, Classification.TREE);
                    if(profile == null) continue;

                    if(profile.type == TFCContent.Type.LEAVES && trunkHeight == 0) {
                        treeType = profile;
                        continue;
                    }
                    if(!hasTree && profile.type == TFCContent.Type.LOG && treeType != null && profile.name.equals(treeType.name)) {
                        trunkHeight++;
                        hasTree = trunkHeight > 2;
                        trunkY = y;
                    }
                    if(profile.type.classification == Classification.DISCOVERY) {
                        discovery = profile;
                        continue;
                    }
                    if(!hasSoil && profile.type.classification == Classification.SOIL) {
                        if(hasTree && trunkY == y+1) {
                            tree[x][z] = treeType;
                            treeType = null;
                            hasTree = false;
                        }

                        soil[x][z] = profile;
                        hasSoil = true;
                        if(discovery != null) {
                            find[x][z] = discovery;
                            discovery = null;
                        }
                        continue;
                    }
                    if(profile.type.classification == Classification.ROCK) {
                        rock[x][z] = profile;
                        if(discovery != null) {
                            find[x][z] = discovery;
                        }
                        break;
                    }
                } while (--y > level.getMinBuildHeight());
            }
        }

        return new SurfaceMD(tree, find, soil, rock);
    }
}
