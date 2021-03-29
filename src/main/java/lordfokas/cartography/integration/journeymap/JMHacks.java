package lordfokas.cartography.integration.journeymap;

import journeymap.client.api.display.Context.MapType;
import journeymap.client.model.MapType.Name;
import lordfokas.cartography.EnumBuster;

import java.util.EnumMap;

public class JMHacks {
    private static final EnumMap<Name, MapType> INTERNAL_API = new EnumMap<>(Name.class);
    private static final EnumMap<MapType, Name> API_INTERNAL = new EnumMap<>(MapType.class);
    private static final EnumBuster<Name> NAMES = new EnumBuster<>(Name.class);
    private static final EnumBuster<MapType> TYPES = new EnumBuster<>(MapType.class);

    public static final Name ISOHYETAL, ISOTHERMAL, BIOGEOGRAPHICAL, GEOLOGICAL;

    public static Name lookup(MapType type){
        if(!API_INTERNAL.containsKey(type))
            return Name.day;
        return API_INTERNAL.get(type);
    }

    public static MapType lookup(Name name){
        if(!API_INTERNAL.containsKey(name))
            return MapType.Any;
        return INTERNAL_API.get(name);
    }

    public static void init(){
        System.out.println("Created new instances of MapType.Name");
        System.out.println(ISOHYETAL);
        System.out.println(ISOTHERMAL);
        System.out.println(BIOGEOGRAPHICAL);
        System.out.println(GEOLOGICAL);
    }

    static {
        ISOHYETAL = make("isohyetal");
        ISOTHERMAL = make("isothermal");
        BIOGEOGRAPHICAL = make("biogeographical");
        GEOLOGICAL = make("geological");

        map(Name.day, MapType.Day);
        map(Name.night, MapType.Night);
        map(Name.topo, MapType.Topo);
        map(Name.underground, MapType.Underground);
    }

    private static Name make(String str){
        Name name = NAMES.make(str);
        MapType type = TYPES.make(str);
        map(name, type);
        return name;
    }

    private static void map(Name name, MapType type){
        INTERNAL_API.put(name, type);
        API_INTERNAL.put(type, name);
    }
}
