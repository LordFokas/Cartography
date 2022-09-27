package lordfokas.cartography.feature.environment.climate;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.data.SerializableDataPool;

public class ClimateDataPool extends SerializableDataPool<ChunkPos, Isoline> {
    private final String value, unit;

    public ClimateDataPool(IStorageAccess storage, ResourceLocation node, ClimateClusterRealm clusterer, String value, String unit) {
        super(storage, node);
        this.value = value;
        this.unit = unit;
        addConsumer(clusterer);
        load();
    }

    @Override
    protected void load(MinecraftStreams.Input stream) throws IOException {
        int count = stream.readInt();
        if(count == 0) return;
        HashMap<ChunkPos, Isoline> data = new HashMap<>();

        for(int i = 0; i < count; i++) {
            ChunkPos pos = new ChunkPos(stream.readLong());
            data.put(pos, Isoline.of(pos, value, unit, stream.readFloat(), stream.readInt(), stream.readInt()));
        }

        setData(data);
    }

    @Override
    protected synchronized void save(MinecraftStreams.Output stream) throws IOException {
        stream.writeInt(pool.size());
        for(Isoline line : pool.values()) {
            Isoline.Curve curve = line.curves.values().iterator().next();
            stream.writeLong(curve.chunk.toLong());
            stream.writeFloat(curve.angle);
            stream.writeInt(curve.mx);
            stream.writeInt(curve.my);
        }
    }
}
