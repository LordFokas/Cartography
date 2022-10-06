package lordfokas.cartography.feature.mapping.climate;

import java.io.IOException;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.CartographyReferences;

public class ClimateSerializer implements DataType<ClimateMD> {
    @Override
    public void serialize(MinecraftStreams.Output output, ClimateMD md) throws IOException {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                output.writeFloat(md.rainfall[x][z]);
                output.writeFloat(md.temperature[x][z]);
            }
        }
    }

    @Override
    public ClimateMD deserialize(MinecraftStreams.Input input) throws IOException {
        float[][] rainfall = new float[16][16];
        float[][] temperature = new float[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                rainfall[x][z] = input.readFloat();
                temperature[x][z] = input.readFloat();
            }
        }

        return new ClimateMD(rainfall, temperature);
    }

    @Override
    public BlazeRegistry.Key<?> getID() {
        return CartographyReferences.MasterData.CLIMATE;
    }
}
