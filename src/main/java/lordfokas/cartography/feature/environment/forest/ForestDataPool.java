package lordfokas.cartography.feature.environment.forest;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.data.SerializableDataPool;

public class ForestDataPool extends SerializableDataPool<RegionPos, HashMap<String, Integer>> {
    public ForestDataPool(IStorageAccess storage, ResourceLocation node) {
        super(storage, node);
    }

    @Override
    protected void load(MinecraftStreams.Input stream) throws IOException {
        HashMap<RegionPos, HashMap<String, Integer>> data = new HashMap<>();
        int regions = stream.readInt();
        for(int r = 0; r < regions; r++) {
            RegionPos region = stream.readRegionPos();
            HashMap<String, Integer> forest = new HashMap<>();
            int trees = stream.readByte();
            for(int t = 0; t < trees; t++) {
                String tree = stream.readUTF();
                int count = stream.readInt();
                forest.put(tree, count);
            }
            data.put(region, forest);
        }
        setData(data);
    }

    @Override
    protected void save(MinecraftStreams.Output stream) throws IOException {
        stream.writeInt(pool.size());
        for(var entry : pool.entrySet()) {
            stream.writeRegionPos(entry.getKey());
            HashMap<String, Integer> forests = entry.getValue();
            stream.writeByte(forests.size());
            for(var forest : forests.entrySet()) {
                stream.writeUTF(forest.getKey());
                stream.writeInt(forest.getValue());
            }
        }
    }
}
