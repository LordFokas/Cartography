package lordfokas.cartography.feature.mapping.ground;

import java.util.Arrays;
import java.util.Objects;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.TFCContent.Profile;

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
    public boolean equalsMD(MasterDatum md) {
        GroundCompositionMD other = (GroundCompositionMD) md;
        return Arrays.equals(this.soil, other.soil, (a, b) -> Arrays.compare(a, b, (c, d) -> Objects.equals(c, d) ? 0 : -1))
            && Arrays.equals(this.rock, other.rock, (a, b) -> Arrays.compare(a, b, (c, d) -> Objects.equals(c, d) ? 0 : -1));
    }
}
