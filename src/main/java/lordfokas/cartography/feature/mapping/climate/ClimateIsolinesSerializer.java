package lordfokas.cartography.feature.mapping.climate;

import java.io.IOException;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.CartographyReferences;

public class ClimateIsolinesSerializer implements DataType<ClimateIsolinesMD> {
    @Override
    public void serialize(MinecraftStreams.Output output, ClimateIsolinesMD md) throws IOException {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                output.writeShort(md.rainfall[x][z]);
                output.writeShort(md.temperature[x][z]);
            }
        }
    }

    @Override
    public ClimateIsolinesMD deserialize(MinecraftStreams.Input input) throws IOException {
        int[][] rainfall = new int[16][16];
        int[][] temperature = new int[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                rainfall[x][z] = input.readShort();
                temperature[x][z] = input.readShort();
            }
        }

        return new ClimateIsolinesMD(rainfall, temperature);
    }

    @Override
    public BlazeRegistry.Key<?> getID() {
        return CartographyReferences.MasterData.CLIMATE_ISO;
    }
}
