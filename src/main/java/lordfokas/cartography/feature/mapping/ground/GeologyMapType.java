package lordfokas.cartography.feature.mapping.ground;

import com.eerussianguy.blazemap.api.BlazeMapReferences;
import com.eerussianguy.blazemap.api.mapping.MapType;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;

public class GeologyMapType extends MapType {
    public GeologyMapType() {
        super(
            CartographyReferences.MapTypes.GEOLOGY,
            Cartography.lang("map.geology"),
            Cartography.resource("icons/maps/geology.png"),

            CartographyReferences.Layers.GEOLOGY,
            BlazeMapReferences.Layers.TERRAIN_ISOLINES,
            BlazeMapReferences.Layers.WATER_LEVEL
        );
    }
}