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
import lordfokas.cartography.utils.TFCBlockTypes;

public class EcosystemLayer extends CartographyLayer {
    public EcosystemLayer() {
        super(
            CartographyReferences.Layers.ECOSYSTEM,
            Cartography.lang("layer.ecosystem"),

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
            counter.consume(relevantData(resolution, x, z, ground.soil, TFCBlockTypes.Profile.class));
            TFCBlockTypes.Profile soil = counter.getDominantProfile();
            counter.consume(relevantData(resolution, x, z, ground.rock, TFCBlockTypes.Profile.class));
            TFCBlockTypes.Profile rock = counter.getDominantProfile();

            if(soil == null && rock == null) return;

            ResourceLocation path = TFCBlockTypes.getTexturePath(soil != null ? soil : rock);
            NativeImage texture = ImageHandler.getImage(path);
            tile.setPixelRGBA(x, z, texture.getPixelRGBA(xOff + x, zOff + z));
        });

        return true;
    }
}
