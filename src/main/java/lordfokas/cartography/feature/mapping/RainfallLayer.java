package lordfokas.cartography.feature.mapping;

import com.eerussianguy.blazemap.api.mapping.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.data.ClimateMD;
import lordfokas.cartography.utils.ColorScale;
import lordfokas.cartography.utils.Colors;

public class RainfallLayer extends Layer {
    private static final float MAX_RAINFALL = 500F;
    private static final ColorScale SCALE = new ColorScale(0F, 300F);

    public RainfallLayer() {
        super(
            CartographyReferences.Layers.RAINFALL,
            Cartography.lang("layer.rainfall"),

            CartographyReferences.Collectors.CLIMATE
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, IDataSource data) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.Collectors.CLIMATE);

        for(int x = 0; x < 16; x++){
            for(int y = 0; y < 16; y++){
                float rainfall = climate.rainfall[x][y] / MAX_RAINFALL;
                float hue = Colors.normalizeHue(SCALE.interpolate(rainfall));
                tile.setPixelRGBA(x, y, Colors.HSB2ABGR(hue, 0.65F, 1F));
            }
        }

        return true;
    }
}
