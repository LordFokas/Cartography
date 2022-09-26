package lordfokas.cartography.data;

import java.io.IOException;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.event.ServerJoinedEvent;
import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import com.eerussianguy.blazemap.engine.BlazeMapEngine;
import com.eerussianguy.blazemap.engine.async.DebouncingDomain;

public abstract class SerializableDataPool<C, D> extends DataPool<C, D> {
    private static DebouncingDomain<SerializableDataPool<?, ?>> debouncer;
    private final ResourceLocation node;
    private final IStorageAccess storage;

    @SubscribeEvent
    public static void onServerJoined(ServerJoinedEvent event) {
        if(debouncer != null) {
            BlazeMapEngine.debouncer().remove(debouncer);
        }
        debouncer = new DebouncingDomain<>(SerializableDataPool::save, 5000, 30000);
        BlazeMapEngine.debouncer().add(debouncer);
    }

    public SerializableDataPool(IStorageAccess storage, ResourceLocation node) {
        this.storage = storage;
        this.node = node;
    }

    public void load() {
        if(!storage.exists(node)) return;
        try(MinecraftStreams.Input stream = storage.read(node)) {
            load(stream);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try(MinecraftStreams.Output stream = storage.write(node)) {
            save(stream);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void markDirty() {
        debouncer.push(this);
    }

    protected abstract void load(MinecraftStreams.Input stream) throws IOException;

    protected abstract void save(MinecraftStreams.Output stream) throws IOException;

    @Override
    public void addData(C coordinate, D data) {
        super.addData(coordinate, data);
        markDirty();
    }

    @Override
    public void setData(Map<C, D> pool) {
        super.setData(pool);
        markDirty();
    }

    @Override
    public void removeData(C coordinate, D data) {
        super.removeData(coordinate, data);
        markDirty();
    }
}
