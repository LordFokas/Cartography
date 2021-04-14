package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.*;
import lordfokas.cartography.core.mapping.Colors;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.discrete.DiscreteDatum;
import lordfokas.cartography.core.mapping.discrete.IDiscreteDataSource;
import lordfokas.cartography.core.mapping.IMapRenderer;
import lordfokas.cartography.core.markers.IMarkerHandler;
import lordfokas.cartography.modules.biology.Biology;
import lordfokas.cartography.modules.biology.TreeCounter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;

import java.awt.image.BufferedImage;

public class SoilMapRenderer implements IMapRenderer {
    private static final int FOREST_GREEN = 0xFF008000;
    private final TerrainHeightSource topo;
    private final IDiscreteDataSource source;
    private final IDiscreteDataSource trees;

    public SoilMapRenderer(IDiscreteDataSource source, TerrainHeightSource topo, IDiscreteDataSource trees){
        this.source = source;
        this.topo = topo;
        this.trees = trees;
    }

    @Override
    public boolean render(BufferedImage image, IChunkData chunk, IMarkerHandler markers) {
        TreeCounter counter = new TreeCounter();

        for(int x = 0; x < 16; x++)
        for(int y = 0; y < 16; y++){
            DiscreteDatum datum = source.getDatum(chunk, x, y);
            ResourceLocation path = TFCBlockTypes.getTexturePath(datum.value);
            BufferedImage texture = ImageHandler.getImage(path);
            int pixel = 0xFF000000 | texture.getRGB(x, y);

            TerrainDatum terrain = topo.getDatum(chunk, x, y);
            if(terrain.water) {
                float d = ((float) Math.min(12, Math.max(0, terrain.depth - 3)) / 12F);
                float h = 0.69F, s = 1F, b = (1F - (d / 2.5F));
                image.setRGB(x, y, 0xFF000000 | Colors.HSB2ARGB(h, s, b));
            }else if(terrain.boundary){
                image.setRGB(x, y, Colors.darken(pixel));
            }else{
                image.setRGB(x, y, pixel);
            }

            DiscreteDatum tree = trees.getDatum(chunk, x, y);
            if(tree != null){
                counter.increment(tree.value);
            }
        }

        ChunkPos pos = chunk.getPos();
        if(pos.x % 16 == 0 ) for(int y = 0; y < 16; y+=2) image.setRGB( 0, y, FOREST_GREEN);
        if(pos.x % 16 == 15) for(int y = 1; y < 16; y+=2) image.setRGB(15, y, FOREST_GREEN);
        if(pos.z % 16 == 0 ) for(int x = 0; x < 16; x+=2) image.setRGB(x,  0, FOREST_GREEN);
        if(pos.z % 16 == 15) for(int x = 1; x < 16; x+=2) image.setRGB(x, 15, FOREST_GREEN);

        Biology.getAsyncTreeDataHandler().setTreesInChunk(chunk, counter.summarize());

        return true;
    }
}
