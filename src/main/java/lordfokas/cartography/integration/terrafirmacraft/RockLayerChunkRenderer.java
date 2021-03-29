package lordfokas.cartography.integration.terrafirmacraft;

import journeymap.client.render.ComparableBufferedImage;
import lordfokas.cartography.integration.journeymap.Colors;
import lordfokas.cartography.integration.journeymap.IChunkData;
import lordfokas.cartography.integration.journeymap.discrete.DiscreteDatum;
import lordfokas.cartography.integration.journeymap.wrapper.CustomChunkRenderer;
import lordfokas.cartography.integration.minecraft.TerrainDatum;
import lordfokas.cartography.integration.minecraft.TerrainHeightDataSource;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class RockLayerChunkRenderer extends CustomChunkRenderer {
    private final TFCRockLayerSource source;
    private final TerrainHeightDataSource topo;

    public RockLayerChunkRenderer(TFCRockLayerSource source, TerrainHeightDataSource topo){
        this.source = source;
        this.topo = topo;
    }

    @Override
    protected boolean render(ComparableBufferedImage image, IChunkData chunk) {
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

    private static final HashMap<String, BufferedImage> CACHE = new HashMap<>();
    private BufferedImage getImage(String rock){
        if(CACHE.containsKey(rock)) return CACHE.get(rock);
        try{
            ResourceLocation tex = new ResourceLocation("tfc", "textures/block/rock/raw/"+rock+".png");
            IResource res = Minecraft.getInstance().getResourceManager().getResource(tex);
            BufferedImage image = ImageIO.read(res.getInputStream());
            CACHE.put(rock, image);
            return image;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
