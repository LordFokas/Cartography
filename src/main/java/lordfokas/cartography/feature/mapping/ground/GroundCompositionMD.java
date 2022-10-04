package lordfokas.cartography.feature.mapping.ground;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.TFCBlockTypes.Profile;

public class GroundCompositionMD extends MasterDatum {
    public final Profile[][] soil;
    public final Profile[][] rock;

    public GroundCompositionMD(Profile[][] soil, Profile[][] rock) {
        this.soil = soil;
        this.rock = rock;
    }

    @Override
    public BlazeRegistry.Key<DataType<MasterDatum>> getID() {
        return CartographyReferences.MasterData.GROUND_COMPOSITION;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
