package lordfokas.cartography.feature.mapping.climate;

import java.util.Arrays;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.CartographyReferences;

public class ClimateIsolinesMD extends MasterDatum {
    public static final int NONE = -100;
    public final int[][] temperature;
    public final int[][] rainfall;

    public ClimateIsolinesMD(int[][] rainfall, int[][] temperature) {
        this.rainfall = rainfall;
        this.temperature = temperature;
    }

    @Override
    public BlazeRegistry.Key<DataType<MasterDatum>> getID() {
        return CartographyReferences.MasterData.CLIMATE_ISO;
    }

    @Override
    public boolean equalsMD(MasterDatum md) {
        ClimateIsolinesMD other = (ClimateIsolinesMD) md;
        return Arrays.equals(this.rainfall, other.rainfall, Arrays::compare)
            && Arrays.equals(this.temperature, other.temperature, Arrays::compare);
    }
}
