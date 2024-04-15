package lordfokas.cartography.feature.discovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.data.SerializableDataPool;

public class DiscoveryDataPool extends SerializableDataPool<BlockPos, DiscoveryState> {
    private final String type;
    private final DiscoveryClusterRealm clusters;

    public DiscoveryDataPool(IStorageAccess storage, ResourceLocation node, DiscoveryClusterRealm consumer, String type) {
        super(storage, node);
        addConsumer(consumer);
        this.clusters = consumer;
        this.type = type;
        load();
    }

    public void asClustered(Consumer<DiscoveryClusterRealm> consumer) {
        consumer.accept(clusters);
    }

    @Override
    protected void load(MinecraftStreams.Input stream) throws IOException {
        int size = stream.readInt();
        if(size == 0) return;

        HashMap<BlockPos, DiscoveryState> data = new HashMap<>();
        for(int i = 0; i < size; i++) {
            data.put(stream.readBlockPos(), new DiscoveryState(stream.readBoolean()));
        }
        setData(data);
    }

    @Override
    protected synchronized void save(MinecraftStreams.Output stream) throws IOException {
        int size = pool.size();
        stream.writeInt(size);
        if(size == 0) return;

        for(var entry : pool.entrySet()) {
            stream.writeBlockPos(entry.getKey());
            stream.writeBoolean(entry.getValue().isDepleted());
        }
    }
}
