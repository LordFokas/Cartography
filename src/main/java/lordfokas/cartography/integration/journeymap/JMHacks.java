package lordfokas.cartography.integration.journeymap;

import journeymap.client.api.display.Context.MapType;
import journeymap.client.model.MapType.Name;
import lordfokas.cartography.EnumBuster;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class JMHacks {
    private static Name[] names;
    private static final HashMap<Name, MapType> INTERNAL_API = new HashMap<>();
    private static final HashMap<MapType, Name> API_INTERNAL = new HashMap<>();
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

    public static boolean isCustom(Name name){
        return name == ISOHYETAL || name == ISOTHERMAL || name == GEOLOGICAL;
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

        performDisgustingHackery();

        map(Name.day, MapType.Day);
        map(Name.night, MapType.Night);
        map(Name.topo, MapType.Topo);
        map(Name.underground, MapType.Underground);

        for(Name name : Name.values()){
            System.err.println(name);
            System.err.println(Name.valueOf(name.name()));
            System.err.println("-----------------------");
        }
    }


    private static void performDisgustingHackery(){
        try{
            Field field = Name.class.getDeclaredField("$VALUES");
            field.setAccessible(true);
            Name[] values = (Name[]) field.get(null);
            LinkedList<Name> list = new LinkedList<>(Arrays.asList(values));
            list.add(ISOHYETAL);
            list.add(ISOTHERMAL);
            list.add(GEOLOGICAL);
            JMHacks.names = list.toArray(values);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static Name[] getAllNames(){
        if(names == null) return new Name[]{};
        Name[] buffer = new Name[names.length];
        System.arraycopy(names, 0, buffer, 0, names.length);
        return buffer;
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
