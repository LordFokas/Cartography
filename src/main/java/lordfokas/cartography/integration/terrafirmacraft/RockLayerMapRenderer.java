package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.*;
import lordfokas.cartography.core.discrete.DiscreteDatum;
import lordfokas.cartography.integration.minecraft.TerrainDatum;
import lordfokas.cartography.integration.minecraft.TerrainHeightDataSource;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class RockLayerMapRenderer implements IMapRenderer {
    private final TFCRockLayerSource source;
    private final TerrainHeightDataSource topo;

    public RockLayerMapRenderer(TFCRockLayerSource source, TerrainHeightDataSource topo){
        this.source = source;
        this.topo = topo;
    }

    @Override
    public boolean render(BufferedImage image, IChunkData chunk, ILabelPlacer placer) {
        for(int x = 0; x < 16; x++)
        for(int y = 0; y < 16; y++){
            DiscreteDatum datum = source.getDatum(chunk, x, y);
            if(datum.boundary){
                image.setRGB(x, y, 0xFF000000 | datum.value.hashCode());
            }else{
                TerrainDatum terrain = topo.getDatum(chunk, x, y);
                if(terrain.water) {
                    float d = ((float) Math.min(12, Math.max(0, terrain.depth - 3)) / 12F);
                    float h = 0.69F, s = 1F, b = (1F - (d / 2.5F));
                    image.setRGB(x, y, 0xFF000000 | Colors.HSB2ARGB(h, s, b));
                }else if(terrain.boundary){
                    image.setRGB(x, y, 0xFF000000 | datum.value.hashCode());
                }else{
                    BufferedImage texture = getImage(datum.value);
                    int pixel = 0xFF000000 | texture.getRGB(x, y);
                    image.setRGB(x, y, pixel);
                }
            }
        }
        return true;
    }

    private BufferedImage getImage(String rock){
        return ImageHandler.getImage(new ResourceLocation("tfc", "textures/block/rock/raw/"+rock+".png"));
    }
}
