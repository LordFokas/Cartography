package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.pipeline.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;

public class TemperatureIsolinesLayer extends Layer {
    public static final int TEMPERATURE_RED = 0xFF0000DD;

    public TemperatureIsolinesLayer() {
        super(
            CartographyReferences.Layers.TEMPERATURE_ISO,
            Cartography.lang("layer.temperature_iso"),
            Cartography.resource("icons/layers/temperature.png"),

            CartographyReferences.MasterData.CLIMATE
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, IDataSource data) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.MasterData.CLIMATE);

        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 16; y++) {
                boolean isBorder = false;
                float value = temperature(climate, x, y, 0);
                isBorder = delta(value, temperature(climate, x + 1, y, value), isBorder);
                isBorder = delta(value, temperature(climate, x - 1, y, value), isBorder);
                isBorder = delta(value, temperature(climate, x, y + 1, value), isBorder);
                isBorder = delta(value, temperature(climate, x, y - 1, value), isBorder);
                if(isBorder) {
                    tile.setPixelRGBA(x, y, TEMPERATURE_RED);
                }
            }
        }

        return true;
    }

    private static float temperature(ClimateMD climate, int x, int z, float def) {
        return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? climate.temperature[x][z] : def;
    }

    private static boolean delta(float pixel, float neighbor, boolean prev) {
        return prev || Math.floor(pixel) == Math.ceil(neighbor);
    }
}
