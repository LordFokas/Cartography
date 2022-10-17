package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.maps.TileResolution;
import com.eerussianguy.blazemap.api.util.ArrayAggregator;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.CartographyLayer;

public class TemperatureIsolinesLayer extends CartographyLayer {
    public static final int TEMPERATURE_RED = 0xFF0000DD;

    public TemperatureIsolinesLayer() {
        super(
            CartographyReferences.Layers.TEMPERATURE_ISO,
            Cartography.lang("layer.temperature_iso"),
            Cartography.resource("icons/layers/temperature.png"),

            CartographyReferences.MasterData.CLIMATE_ISO
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, TileResolution resolution, IDataSource data, int xGridOffset, int zGridOffset) {
        ClimateIsolinesMD climate = (ClimateIsolinesMD) data.get(CartographyReferences.MasterData.CLIMATE_ISO);

        foreachPixel(resolution, (x, z) -> {
            int value = ArrayAggregator.max(relevantData(resolution, x, z, climate.temperature));
            if(value != ClimateIsolinesMD.NONE){
                tile.setPixelRGBA(x, z, TEMPERATURE_RED);
            }
        });

        return true;
    }
}
