package lordfokas.cartography.feature.mapping.climate;

import net.minecraft.client.gui.components.Widget;

import com.eerussianguy.blazemap.api.mapping.Layer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.ColorScale;
import lordfokas.cartography.utils.Colors;

public class RainfallLayer extends Layer {
    public static final float MAX_RAINFALL = 500F;
    private static final ColorScale SCALE = new ColorScale(0F, 240F);

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

        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 16; y++) {
                float rainfall = climate.rainfall[x][y] / MAX_RAINFALL;
                float hue = Colors.normalizeHue(SCALE.interpolate(rainfall));
                tile.setPixelRGBA(x, y, Colors.HSB2ABGR(hue, 0.65F, 1F));
            }
        }

        return true;
    }

    @Override
    public Widget getLegendWidget() {
        return new RainfallLegendWidget();
    }

    public static NativeImage getLegend() {
        int height = 100;
        NativeImage legend = new NativeImage(1, height, false);
        for(int y = 0; y < height; y++) {
            float hue = Colors.normalizeHue(SCALE.interpolate(((float) y) / height));
            legend.setPixelRGBA(0, (height - y) - 1, Colors.HSB2ABGR(hue, 0.65F, 1F));
        }
        return legend;
    }
}
