package lordfokas.cartography.feature.mapping;

import com.eerussianguy.blazemap.api.mapping.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.data.ClimateMD;
import lordfokas.cartography.utils.ColorScale;
import lordfokas.cartography.utils.Colors;

public class TemperatureLayer extends Layer {
    private static final float MAX_TEMP = 40F;
    private static final float MIN_TEMP = -20F;
    private static final float DELTA = MAX_TEMP - MIN_TEMP;
    private static final ColorScale SCALE = new ColorScale(300F, 0F);

    public TemperatureLayer() {
        super(
            CartographyReferences.Layers.TEMPERATURE,
            Cartography.lang("layer.temperature"),

            CartographyReferences.Collectors.CLIMATE
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, IDataSource data) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.Collectors.CLIMATE);

        for(int x = 0; x < 16; x++){
            for(int y = 0; y < 16; y++){
                float temperature = (climate.temperature[x][y] - MIN_TEMP) / DELTA;
                float hue = Colors.normalizeHue(SCALE.interpolate(temperature));
                tile.setPixelRGBA(x, y, Colors.HSB2ABGR(hue, 0.65F, 1F));
            }
        }

        return true;
    }
}
