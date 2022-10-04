package lordfokas.cartography.feature.discovery;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.data.DataFlow;
import lordfokas.cartography.data.SerializableDataPool;

public class DiscoveryDataPool extends SerializableDataPool<BlockPos, String> {
    private final String type;

    public DiscoveryDataPool(IStorageAccess storage, ResourceLocation node, DataFlow.IDataConsumer<BlockPos, String> consumer, String type) {
        super(storage, node);
        addConsumer(consumer);
        this.type = type;
        load();
    }

    @Override
    protected void load(MinecraftStreams.Input stream) throws IOException {
        int size = stream.readInt();
        if(size == 0) return;

        HashMap<BlockPos, String> data = new HashMap<>();
        for(int i = 0; i < size; i++) {
            data.put(stream.readBlockPos(), type);
        }
        setData(data);
    }

    @Override
    protected synchronized void save(MinecraftStreams.Output stream) throws IOException {
        int size = pool.size();
        stream.writeInt(size);
        if(size == 0) return;

        for(BlockPos pos : pool.keySet()) {
            stream.writeBlockPos(pos);
        }
    }
}
