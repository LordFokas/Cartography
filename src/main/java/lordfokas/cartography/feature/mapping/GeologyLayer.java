package lordfokas.cartography.feature.mapping;

import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.mapping.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.data.GroundCompositionMD;
import lordfokas.cartography.utils.TFCBlockTypes;
import lordfokas.cartography.utils.ImageHandler;

public class GeologyLayer extends Layer {
    public GeologyLayer() {
        super(
            CartographyReferences.Layers.GEOLOGY,
            Cartography.lang("layer.geology"),

            CartographyReferences.Collectors.GROUND_COMPOSITION
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, IDataSource data) {
        GroundCompositionMD ground = (GroundCompositionMD) data.get(CartographyReferences.Collectors.GROUND_COMPOSITION);
        for(int x = 0; x < 16; x++){
            for(int y = 0; y < 16; y++){
                TFCBlockTypes.Profile rock = ground.rock[x][y];
                if(rock == null) continue;
                ResourceLocation path = TFCBlockTypes.getTexturePath(rock);
                NativeImage texture = ImageHandler.getImage(path);
                tile.setPixelRGBA(x, y, texture.getPixelRGBA(x, y));
            }
        }
        return true;
    }
}