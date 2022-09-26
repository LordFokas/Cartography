package lordfokas.cartography.feature.data;

import net.minecraft.nbt.CompoundTag;

import com.eerussianguy.blazemap.api.mapping.MasterDatum;
import lordfokas.cartography.utils.TFCBlockTypes.Profile;

public class GroundCompositionMD implements MasterDatum {
    public final Profile[][] soil;
    public final Profile[][] rock;

    public GroundCompositionMD(Profile[][] soil, Profile[][] rock) {
        this.soil = soil;
        this.rock = rock;
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
