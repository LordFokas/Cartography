package lordfokas.cartography.feature.mapping.ground;

import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.pipeline.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.utils.TFCBlockTypes;

public class EcosystemLayer extends Layer {
    public EcosystemLayer() {
        super(
            CartographyReferences.Layers.ECOSYSTEM,
            Cartography.lang("layer.ecosystem"),

            CartographyReferences.MasterData.GROUND_COMPOSITION
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, IDataSource data) {
        GroundCompositionMD ground = (GroundCompositionMD) data.get(CartographyReferences.MasterData.GROUND_COMPOSITION);
        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 16; y++) {
                TFCBlockTypes.Profile soil = ground.soil[x][y];
                if(soil != null) {
                    ResourceLocation path = TFCBlockTypes.getTexturePath(soil);
                    NativeImage texture = ImageHandler.getImage(path);
                    tile.setPixelRGBA(x, y, texture.getPixelRGBA(x, y));
                }
                else {
                    TFCBlockTypes.Profile rock = ground.rock[x][y];
                    if(rock == null) continue;
                    ResourceLocation path = TFCBlockTypes.getTexturePath(rock);
                    NativeImage texture = ImageHandler.getImage(path);
                    tile.setPixelRGBA(x, y, texture.getPixelRGBA(x, y));
                }
            }
        }
        return true;
    }
}
