package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.*;
import lordfokas.cartography.core.mapping.Colors;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.discrete.DiscreteDatum;
import lordfokas.cartography.core.mapping.discrete.IDiscreteDataSource;
import lordfokas.cartography.core.mapping.IMapRenderer;
import lordfokas.cartography.core.markers.IMarkerHandler;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class RockLayerMapRenderer implements IMapRenderer {
    private final IDiscreteDataSource source;
    private final TerrainHeightSource topo;

    public RockLayerMapRenderer(IDiscreteDataSource source, TerrainHeightSource topo){
        this.source = source;
        this.topo = topo;
    }

    @Override
    public boolean render(BufferedImage image, IChunkData chunk, IMarkerHandler placer) {
        for(int x = 0; x < 16; x++)
        for(int y = 0; y < 16; y++){
            DiscreteDatum datum = source.getDatum(chunk, x, y);
            ResourceLocation path = TFCBlockTypes.getTexturePath(datum.value);
            if(path == null) continue;
            BufferedImage texture = ImageHandler.getImage(path);
            if(texture == null) continue;
            int pixel = 0xFF000000 | texture.getRGB(x, y);

            if(datum.boundary){
                image.setRGB(x, y, Colors.darken(pixel));
            }else{
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
            }
        }
        return true;
    }
}
