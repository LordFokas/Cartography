package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.BlazeMapReferences;
import com.eerussianguy.blazemap.api.mapping.MapType;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;

public class RainfallMapType extends MapType {

    public RainfallMapType() {
        super(
            CartographyReferences.MapTypes.RAINFALL,
            Cartography.lang("map.rainfall"),
            Cartography.resource("icons/maps/rainfall.png"),

            CartographyReferences.Layers.RAINFALL,
            BlazeMapReferences.Layers.TERRAIN_ISOLINES,
            BlazeMapReferences.Layers.WATER_LEVEL,
            CartographyReferences.Layers.TEMPERATURE_ISO,
            CartographyReferences.Layers.RAINFALL_ISO
        );
    }
}
