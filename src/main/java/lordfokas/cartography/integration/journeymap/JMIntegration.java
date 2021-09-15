package lordfokas.cartography.integration.journeymap;

import journeymap.client.cartography.render.BaseRenderer;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.MapTypeRegistry;

import java.util.HashMap;

public class JMIntegration {
    private static final HashMap<MapType, CustomChunkRenderer> renderers = new HashMap<>();

    public static void createMaps(){
        JMHacks.init(MapTypeRegistry.MASTER.getTypes());
    }

    public static BaseRenderer getRenderer(MapType type){
        return renderers.computeIfAbsent(type, t -> new CustomChunkRenderer(MapTypeRegistry.MASTER.get(t)));
    }
}
