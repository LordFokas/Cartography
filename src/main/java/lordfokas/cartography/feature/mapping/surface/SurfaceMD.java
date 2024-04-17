package lordfokas.cartography.feature.mapping.surface;

import java.util.Arrays;
import java.util.Objects;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.TFCContent.Profile;

public class SurfaceMD extends MasterDatum {
    public final Profile[][] soil;
    public final Profile[][] rock;
    public final Profile[][] discoveries;

    public SurfaceMD(Profile[][] soil, Profile[][] rock, Profile[][] discoveries) {
        this.soil = soil;
        this.rock = rock;
        this.discoveries = discoveries;
    }

    @Override
    public BlazeRegistry.Key<DataType<MasterDatum>> getID() {
        return CartographyReferences.MasterData.SURFACE;
    }

    @Override
    public boolean equalsMD(MasterDatum md) {
        SurfaceMD other = (SurfaceMD) md;
        return Arrays.equals(this.soil,        other.soil,        (a, b) -> Arrays.compare(a, b, (c, d) -> Objects.equals(c, d) ? 0 : -1))
            && Arrays.equals(this.rock,        other.rock,        (a, b) -> Arrays.compare(a, b, (c, d) -> Objects.equals(c, d) ? 0 : -1))
            && Arrays.equals(this.discoveries, other.discoveries, (a, b) -> Arrays.compare(a, b, (c, d) -> Objects.equals(c, d) ? 0 : -1));
    }
}
