package lordfokas.cartography.feature.mapping.surface;

import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.maps.TileResolution;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.CartographyLayer;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.feature.environment.ProfileCounter;
import lordfokas.cartography.feature.TFCContent;

public class EcosystemLayer extends CartographyLayer {
    public EcosystemLayer() {
        super(
            CartographyReferences.Layers.ECOSYSTEM,
            Cartography.lang("layer.ecosystem"),

            CartographyReferences.MasterData.SURFACE
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, TileResolution resolution, IDataSource data, int xGridOffset, int zGridOffset) {
        SurfaceMD surface = (SurfaceMD) data.get(CartographyReferences.MasterData.SURFACE);
        final int xOff = xGridOffset * resolution.chunkWidth;
        final int zOff = zGridOffset * resolution.chunkWidth;

        foreachPixel(resolution, (x, z) -> {
            ProfileCounter counter = COUNTERS.get();
            counter.consume(relevantData(resolution, x, z, surface.soil, TFCContent.Profile.class));
            TFCContent.Profile soil = counter.getDominantProfile();
            counter.consume(relevantData(resolution, x, z, surface.rock, TFCContent.Profile.class));
            TFCContent.Profile rock = counter.getDominantProfile();

            if(soil == null && rock == null) return;

            ResourceLocation path = TFCContent.getTexturePath(soil != null ? soil : rock);
            NativeImage texture = ImageHandler.getImage(path);
            tile.setPixelRGBA(x, z, texture.getPixelRGBA(xOff + x, zOff + z));
        });

        return true;
    }
}
