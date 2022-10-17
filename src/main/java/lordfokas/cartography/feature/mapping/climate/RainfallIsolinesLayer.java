package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.maps.TileResolution;
import com.eerussianguy.blazemap.api.util.ArrayAggregator;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.CartographyLayer;

public class RainfallIsolinesLayer extends CartographyLayer {
    public static final int RAINFALL_BLUE = 0xFFFF6600;

    public RainfallIsolinesLayer() {
        super(
            CartographyReferences.Layers.RAINFALL_ISO,
            Cartography.lang("layer.rainfall_iso"),
            Cartography.resource("icons/layers/rainfall.png"),

            CartographyReferences.MasterData.CLIMATE_ISO
        );
    }

    @Override
    public boolean renderTile(NativeImage tile, TileResolution resolution, IDataSource data, int xGridOffset, int zGridOffset) {
        ClimateIsolinesMD climate = (ClimateIsolinesMD) data.get(CartographyReferences.MasterData.CLIMATE_ISO);

        foreachPixel(resolution, (x, z) -> {
            int value = ArrayAggregator.max(relevantData(resolution, x, z, climate.rainfall));
            if(value != ClimateIsolinesMD.NONE && value % 10 == 0){
                tile.setPixelRGBA(x, z, RAINFALL_BLUE);
            }
        });

        return true;
    }
}
