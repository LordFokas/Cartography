package lordfokas.cartography.core;

import lordfokas.cartography.Cartography;
import lordfokas.cartography.core.mapping.IMapRenderer;
import lordfokas.cartography.modules.Module;

import java.util.EnumMap;
import java.util.Set;
import java.util.function.BiConsumer;

public class MapTypeRegistry {
    public static final MapTypeRegistry MASTER = new MapTypeRegistry();

    private final Module owner;
    private final EnumMap<MapType, IMapRenderer> renderers = new EnumMap<>(MapType.class);

    private MapTypeRegistry(){ this.owner = null; }

    public MapTypeRegistry(Module owner){
        if(owner == null) throw new NullPointerException("MapTypeRegistry owner Module cannot be null");
        this.owner = owner;
    }

    public void register(MapType type, IMapRenderer renderer){
        if(owner == null)
            throw new IllegalStateException("Cannot register map types directly on master registry");
        if(type.module != owner)
            throw new IllegalArgumentException(String.format("Cannot register map of type %s on registry of type %s", type.module, owner));
        if(renderers.containsKey(type)){
            Cartography.logger().warn("Replacing map of type {} original is {} and new is {}", type, renderers.get(type), renderer);
        }
        renderers.put(type, renderer);
    }

    public void dumpToMaster(){
        if(this.owner == null)
            throw new IllegalStateException("This is already the master MapTypeRegistry, you dumbass!");
        MASTER.renderers.putAll(renderers);
    }

    public void forEach(BiConsumer<MapType, IMapRenderer> visitor){
        for(MapType type : MapType.values()){
            if(renderers.containsKey(type)){
                visitor.accept(type, renderers.get(type));
            }
        }
    }

    public Set<MapType> getTypes(){
        return renderers.keySet();
    }

    public IMapRenderer get(MapType type){
        return renderers.get(type);
    }
}
