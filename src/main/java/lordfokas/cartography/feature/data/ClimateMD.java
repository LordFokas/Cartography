package lordfokas.cartography.feature.data;

import net.minecraft.nbt.CompoundTag;

import com.eerussianguy.blazemap.api.mapping.MasterDatum;

public class ClimateMD implements MasterDatum {
    public final float[][] rainfall;
    public final float[][] temperature;

    public ClimateMD(float[][] rainfall, float[][] temperature){
        this.rainfall = rainfall;
        this.temperature = temperature;
    }

    @Override
    public CompoundTag serialize() {
        return null;
    }

    @Override
    public MasterDatum deserialize(CompoundTag compoundTag) {
        return null;
    }
}
