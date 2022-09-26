package lordfokas.cartography.feature.data;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.data.SerializableDataPool;

public class RockDataPool extends SerializableDataPool<ChunkPos, String> {
    public RockDataPool(IStorageAccess storage, ResourceLocation node, RockClusterRealm clusterer) {
        super(storage, node);
        addConsumer(clusterer);
        load();
    }

    @Override
    protected void load(MinecraftStreams.Input stream) throws IOException {
        int count = stream.readInt();
        if(count == 0) return;

        String rock = stream.readUTF();
        HashMap<ChunkPos, String> data = new HashMap<>();

        for(int i = 0; i < count; i++){
            data.put(new ChunkPos(stream.readLong()), rock);
        }

        setData(data);
    }

    @Override
    protected void save(MinecraftStreams.Output stream) throws IOException {
        int count = pool.size();
        stream.writeInt(count);
        if(count == 0) return;

        String rock = pool.entrySet().iterator().next().getValue();
        stream.writeUTF(rock);

        for(ChunkPos chunk : pool.keySet()){
            stream.writeLong(chunk.toLong());
        }
    }
}
