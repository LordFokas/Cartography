package lordfokas.cartography.feature.mapping.climate;

import net.minecraft.client.gui.components.Widget;

import com.eerussianguy.blazemap.api.maps.TileResolution;
import com.eerussianguy.blazemap.api.util.ArrayAggregator;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.CartographyLayer;
import lordfokas.cartography.utils.ColorScale;
import lordfokas.cartography.utils.Colors;

public class RainfallLayer extends CartographyLayer {
    public static final float MAX_RAINFALL = 500F;
    private static final ColorScale SCALE = new ColorScale(0F, 240F);

    public RainfallLayer() {
        super(
            CartographyReferences.Layers.RAINFALL,
            Cartography.lang("layer.rainfall"),

            CartographyReferences.MasterData.CLIMATE
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, TileResolution resolution, IDataSource data, int xGridOffset, int zGridOffset) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.MasterData.CLIMATE);

        foreachPixel(resolution, (x, z) -> {
            float rainfall = ArrayAggregator.avg(relevantData(resolution, x, z, climate.rainfall)) / MAX_RAINFALL;
            float hue = Colors.normalizeHue(SCALE.interpolate(rainfall));
            tile.setPixelRGBA(x, z, Colors.HSB2ABGR(hue, 0.65F, 1F));
        });

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
