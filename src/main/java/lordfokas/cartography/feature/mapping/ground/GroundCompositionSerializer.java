package lordfokas.cartography.feature.mapping.ground;

import java.io.IOException;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.TFCBlockTypes.*;

public class GroundCompositionSerializer implements DataType<GroundCompositionMD> {
    @Override
    public void serialize(MinecraftStreams.Output output, GroundCompositionMD md) throws IOException {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                writeProfile(output, md.rock[x][z]);
                writeProfile(output, md.soil[x][z]);
            }
        }
    }

    @Override
    public GroundCompositionMD deserialize(MinecraftStreams.Input input) throws IOException {
        Profile[][] rock = new Profile[16][16];
        Profile[][] soil = new Profile[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                rock[x][z] = readProfile(input);
                soil[x][z] = readProfile(input);
            }
        }

        return new GroundCompositionMD(soil, rock);
    }

    private static void writeProfile(MinecraftStreams.Output output, Profile profile) throws IOException {
        if(profile == null){
            output.writeByte(-1);
        }else{
            output.writeByte(profile.type.ordinal());
            output.writeUTF(profile.name);
        }
    }

    private static Profile readProfile(MinecraftStreams.Input input) throws IOException {
        byte id = input.readByte();
        if(id == -1) return null;
        return new Profile(Type.values()[id], input.readUTF());
    }

    @Override
    public BlazeRegistry.Key<?> getID() {
        return CartographyReferences.MasterData.GROUND_COMPOSITION;
    }
}
