package lordfokas.cartography.core.data;

import lordfokas.cartography.Cartography;
import lordfokas.cartography.core.player.IPlayerDataStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class SerializableDataPool<C, D> extends DataPool<C, D> {
    private final DataFlow.IDataSerializer<C, D> codec;
    private final IPlayerDataStore store;
    private final Map<C, D> proxy;
    private final String node;

    public SerializableDataPool(DataFlow.IDataSerializer<C, D> codec, IPlayerDataStore store, String node){
        this.proxy = Collections.unmodifiableMap(pool);
        this.codec = codec;
        this.store = store;
        this.node = node;
    }

    public void load(){
        if(store.isAbsent(node)) return;
        try(
            DataInputStream stream = store.getInputStream(node)
        ){
            deserialize(stream);
            for(DataFlow.IDataConsumer<C, D> consumer : consumers){
                consumer.setData(proxy);
            }
        }catch(IOException e){
            Cartography.logger().error(e);
        }
    }

    public void save(){
        if(store.isAbsent(node) && pool.isEmpty()) return;
        try( DataOutputStream stream = store.getOutputStream(node) ){ serialize(stream); }
        catch(IOException e){ Cartography.logger().error(e); }
    }

    @Override
    public void setData(Map<C, D> input) {
        pool.clear();
        pool.putAll(input);
    }

    protected void deserialize(DataInputStream stream) throws IOException{
        codec.deserialize(stream, this);
    }

    protected void serialize(DataOutputStream stream) throws IOException{
        codec.serialize(stream, this);
    }
}
