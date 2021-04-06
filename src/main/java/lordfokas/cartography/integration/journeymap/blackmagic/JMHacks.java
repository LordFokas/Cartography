package lordfokas.cartography.integration.journeymap.blackmagic;

import journeymap.client.api.display.Context;
import journeymap.client.model.MapType.Name;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.core.MapType;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class JMHacks {
    private static final EnumBuster<Name> NAMES = new EnumBuster<>(Name.class);
    private static final EnumBuster<Context.MapType> TYPES = new EnumBuster<>(Context.MapType.class);
    private static final HashMap<Name, Context.MapType> INTERNAL_API = new HashMap<>();
    private static final HashMap<Context.MapType, Name> API_INTERNAL = new HashMap<>();
    private static final HashMap<Name, MapType> JM_LOCAL = new HashMap<>();
    private static final HashMap<MapType, Name> LOCAL_JM = new HashMap<>();
    private static final HashMap<MapType, Context.MapType> LOCAL_API = new HashMap<>();
    private static List<Name> customNamesOrdered;

    public static Name lookup(Context.MapType type){
        if(!API_INTERNAL.containsKey(type))
            return Name.day;
        return API_INTERNAL.get(type);
    }

    public static Context.MapType lookup(Name name){
        if(!INTERNAL_API.containsKey(name))
            return Context.MapType.Any;
        return INTERNAL_API.get(name);
    }

    public static Context.MapType api(MapType type){
        return LOCAL_API.computeIfAbsent(type, t -> {
            Name name = LOCAL_JM.get(t);
            if(name == null) return null;
            return INTERNAL_API.get(name);
        });
    }

    public static EnumSet<Context.MapType> api(MapType[] items){
        LinkedList<Context.MapType> results = new LinkedList<>();
        for(MapType type : items){
            Context.MapType result = api(type);
            if(result != null){
                results.add(result);
            }
        }
        return EnumSet.copyOf(results);
    }

    public static boolean isCustom(Name name){
        return JM_LOCAL.containsKey(name);
    }

    public static Collection<Name> getCustomNames(){
        return customNamesOrdered;
    }

    public static MapType getCustom(Name name){
        return JM_LOCAL.get(name);
    }

    public static void init(Collection<MapType> types){
        Logger logger = Cartography.logger();
        logger.warn("Injecting dynamic enum entries on JM Name:");
        for(MapType type : types){
            Name name = make(type.name().toLowerCase());
            JM_LOCAL.put(name, type);
            LOCAL_JM.put(type, name);
            logger.warn("Successfully injected {}", name);
        }
        logger.warn("Caching dynamic entries...");
        List<Name> ordered = new ArrayList<>(JM_LOCAL.size());
        for(MapType type : MapType.values()){
            if(types.contains(type)){
                ordered.add(LOCAL_JM.get(type));
            }
        }
        customNamesOrdered = Collections.unmodifiableList(ordered);
        logger.warn("Done injecting dynamic enum entries on JM Name");

        map(Name.day, Context.MapType.Day);
        map(Name.night, Context.MapType.Night);
        map(Name.topo, Context.MapType.Topo);
        map(Name.underground, Context.MapType.Underground);
    }

    private static Name make(String str){
        Name name = NAMES.make(str);
        NAMES.addByValue(name);
        Context.MapType type = TYPES.make(str);
        TYPES.addByValue(type);
        map(name, type);
        return name;
    }

    private static void map(Name name, Context.MapType type){
        INTERNAL_API.put(name, type);
        API_INTERNAL.put(type, name);
    }
}
