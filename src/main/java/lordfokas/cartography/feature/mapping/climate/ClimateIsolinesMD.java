package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.CartographyReferences;

public class ClimateIsolinesMD extends MasterDatum {
    public static final int NONE = -100;
    public final int[][] temperature;
    public final int[][] rainfall;

    public ClimateIsolinesMD(int[][] temperature, int[][] rainfall) {
        this.temperature = temperature;
        this.rainfall = rainfall;
    }

    @Override
    public BlazeRegistry.Key<DataType<MasterDatum>> getID() {
        return CartographyReferences.MasterData.CLIMATE_ISO;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
