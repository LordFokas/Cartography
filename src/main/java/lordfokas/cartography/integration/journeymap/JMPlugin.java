package lordfokas.cartography.integration.journeymap;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Displayable;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.core.markers.IMarkerHandler;
import lordfokas.cartography.core.markers.Marker;
import lordfokas.cartography.integration.journeymap.blackmagic.JMHacks;

import java.util.HashMap;

@ClientPlugin @SuppressWarnings("unused")
public class JMPlugin implements IClientPlugin, IMarkerHandler {
    private static JMPlugin plugin;
    public static JMPlugin instance(){ return plugin; }

    private IClientAPI api;

    @Override
    public String getModId(){ return Cartography.MOD_ID; }

    @Override
    public void initialize(IClientAPI api){
        plugin = this;
        this.api = api;
    }

    @Override
    public void onEvent(ClientEvent evt){

    }

    private static final int SCALE = 4;
    private final HashMap<String, Displayable> displayables = new HashMap<>();

    @Override
    public void place(Marker marker) {

        String key = marker.key;
        if(displayables.containsKey(key)) return;

        MapImage image = new MapImage(marker.image).setColor(marker.tint).setRotation(marker.rotation);
        ImageOverlay label = new ImageOverlay(Cartography.MOD_ID, key, marker.nw, marker.se, image);
        label.setDimension(marker.dim).setDisplayOrder(2000);

        if(marker.maps.length > 0) {
            label.setActiveMapTypes(JMHacks.api(marker.maps));
        }

        try {
            api.show(label);
            displayables.put(key, label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String key) {
        if(!displayables.containsKey(key)) return;
        try {
            api.remove(displayables.get(key));
            displayables.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
