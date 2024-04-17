package lordfokas.cartography.feature.mapping.surface;

import java.io.IOException;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.TFCContent.*;

public class SurfaceSerializer implements DataType<SurfaceMD> {
    @Override
    public void serialize(MinecraftStreams.Output output, SurfaceMD md) throws IOException {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                writeProfile(output, md.rock[x][z]);
                writeProfile(output, md.soil[x][z]);
                writeProfile(output, md.discoveries[x][z]);
            }
        }
    }

    @Override
    public SurfaceMD deserialize(MinecraftStreams.Input input) throws IOException {
        Profile[][] rock = new Profile[16][16];
        Profile[][] soil = new Profile[16][16];
        Profile[][] discoveries = new Profile[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                rock[x][z] = readProfile(input);
                soil[x][z] = readProfile(input);
                discoveries[x][z] = readProfile(input);
            }
        }

        return new SurfaceMD(soil, rock, discoveries);
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
        return CartographyReferences.MasterData.SURFACE;
    }
}
