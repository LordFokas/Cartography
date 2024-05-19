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
                writeProfile(output, md.tree[x][z]);
                writeProfile(output, md.find[x][z]);
                writeProfile(output, md.soil[x][z]);
                writeProfile(output, md.rock[x][z]);
            }
        }
    }

    @Override
    public SurfaceMD deserialize(MinecraftStreams.Input input) throws IOException {
        Profile[][] tree = new Profile[16][16];
        Profile[][] find = new Profile[16][16];
        Profile[][] rock = new Profile[16][16];
        Profile[][] soil = new Profile[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                tree[x][z] = readProfile(input);
                find[x][z] = readProfile(input);
                soil[x][z] = readProfile(input);
                rock[x][z] = readProfile(input);
            }
        }

        return new SurfaceMD(tree, find, soil, rock);
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
        return Profile.lookup(Type.values()[id], input.readUTF());
    }

    @Override
    public BlazeRegistry.Key<?> getID() {
        return CartographyReferences.MasterData.SURFACE;
    }
}
