package lordfokas.cartography.feature.mapping;

import com.eerussianguy.blazemap.api.BlazeMapReferences;
import com.eerussianguy.blazemap.api.mapping.MapType;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;

public class TemperatureMapType extends MapType {
    public TemperatureMapType() {
        super(
            CartographyReferences.MapTypes.TEMPERATURE,
            Cartography.lang("map.temperature"),
            Cartography.resource("icons/maps/temperature.png"),

            CartographyReferences.Layers.TEMPERATURE,
            BlazeMapReferences.Layers.TERRAIN_ISOLINES,
            BlazeMapReferences.Layers.WATER_LEVEL,
            CartographyReferences.Layers.RAINFALL_ISO,
            CartographyReferences.Layers.TEMPERATURE_ISO
        );
    }
}
