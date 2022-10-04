package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.CartographyReferences;

public class ClimateMD extends MasterDatum {
    public final float[][] rainfall;
    public final float[][] temperature;

    public ClimateMD(float[][] rainfall, float[][] temperature) {
        this.rainfall = rainfall;
        this.temperature = temperature;
    }

    @Override
    public BlazeRegistry.Key<DataType<MasterDatum>> getID() {
        return CartographyReferences.MasterData.CLIMATE;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
