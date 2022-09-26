package lordfokas.cartography.feature.mapping;

import com.eerussianguy.blazemap.api.mapping.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.data.ClimateMD;

public class RainfallIsolinesLayer extends Layer {
    public static final int RAINFALL_BLUE = 0xFFFF6600;

    public RainfallIsolinesLayer() {
        super(
            CartographyReferences.Layers.RAINFALL_ISO,
            Cartography.lang("layer.rainfall_iso"),
            Cartography.resource("icons/maps/rainfall.png"),

            CartographyReferences.Collectors.CLIMATE
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, IDataSource data) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.Collectors.CLIMATE);

        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 16; y++) {
                boolean isBorder = false;
                float value = rainfall(climate, x, y, 0);
                isBorder = delta(value, rainfall(climate, x + 1, y, value), isBorder);
                isBorder = delta(value, rainfall(climate, x - 1, y, value), isBorder);
                isBorder = delta(value, rainfall(climate, x, y + 1, value), isBorder);
                isBorder = delta(value, rainfall(climate, x, y - 1, value), isBorder);
                if(isBorder) {
                    tile.setPixelRGBA(x, y, RAINFALL_BLUE);
                }
            }
        }

        return true;
    }

    private static float rainfall(ClimateMD climate, int x, int z, float def) {
        return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? climate.rainfall[x][z] : def;
    }

    private static boolean delta(float pixel, float neighbor, boolean prev) {
        if(prev) return true;
        int value = (int) Math.floor(pixel);
        return value % 10 == 0 && value == Math.ceil(neighbor);
    }
}
