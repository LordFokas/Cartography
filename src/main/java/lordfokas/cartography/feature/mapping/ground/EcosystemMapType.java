package lordfokas.cartography.feature.mapping.ground;

import com.eerussianguy.blazemap.api.BlazeMapReferences;
import com.eerussianguy.blazemap.api.mapping.MapType;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;

public class EcosystemMapType extends MapType {
    public EcosystemMapType() {
        super(
            CartographyReferences.MapTypes.ECOSYSTEM,
            Cartography.lang("map.ecosystem"),
            Cartography.resource("icons/maps/ecosystem.png"),

            CartographyReferences.Layers.ECOSYSTEM,
            BlazeMapReferences.Layers.TERRAIN_ISOLINES,
            BlazeMapReferences.Layers.WATER_LEVEL
        );
    }
}
