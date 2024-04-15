package lordfokas.cartography.feature.mapping.ground;

import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.maps.TileResolution;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.CartographyLayer;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.utils.ProfileCounter;
import lordfokas.cartography.feature.TFCContent;

public class GeologyLayer extends CartographyLayer {
    public GeologyLayer() {
        super(
            CartographyReferences.Layers.GEOLOGY,
            Cartography.lang("layer.geology"),

            CartographyReferences.MasterData.GROUND_COMPOSITION
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, TileResolution resolution, IDataSource data, int xGridOffset, int zGridOffset) {
        GroundCompositionMD ground = (GroundCompositionMD) data.get(CartographyReferences.MasterData.GROUND_COMPOSITION);
        final int xOff = xGridOffset * resolution.chunkWidth;
        final int zOff = zGridOffset * resolution.chunkWidth;

        foreachPixel(resolution, (x, z) -> {
            ProfileCounter counter = COUNTERS.get();
            counter.consume(relevantData(resolution, x, z, ground.rock, TFCContent.Profile.class));
            TFCContent.Profile rock = counter.getDominantProfile();

            if(rock == null) return;
            ResourceLocation path = TFCContent.getTexturePath(rock);
            NativeImage texture = ImageHandler.getImage(path);
            tile.setPixelRGBA(x, z, texture.getPixelRGBA(xOff + x, zOff + z));
        });

        return true;
    }
}
