package lordfokas.cartography.integration.journeymap;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Context;
import journeymap.client.api.display.Displayable;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapImage;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.markers.IMarkerPlacer;
import lordfokas.cartography.core.ImageHandler;
import lordfokas.cartography.integration.journeymap.blackmagic.JMHacks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.HashMap;

@ClientPlugin @SuppressWarnings("unused")
public class JMPlugin implements IClientPlugin, IMarkerPlacer {
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
    private final HashMap<String, Displayable> labels = new HashMap<>();

    @Override
    public void place(RegistryKey<World> dim, int wx, int wz, String text, int hue, DataType type, int abs, int angle, MapType ... types) {

        String key = type.name() +"-"+abs+"-"+ (wx>>6) +"-"+ (wz>>6);
        if(labels.containsKey(key)) return;

        BufferedImage raw = ImageHandler.getLabel(text, SCALE);
        int h = raw.getHeight()/(SCALE*2), w = raw.getWidth()/(SCALE*2);
        MapImage image = new MapImage(raw).setColor(hue).setRotation(angle);
        BlockPos nw = new BlockPos(wx - (w/2), 0, wz - (h/2));
        BlockPos se = nw.offset(w, 0, h);
        ImageOverlay label = new ImageOverlay(Cartography.MOD_ID, key, nw, se, image);
        label.setDimension(dim).setDisplayOrder(2000);

        if(types.length > 0){
            label.setActiveMapTypes(JMHacks.api(types));
        }

        Minecraft.getInstance().submitAsync(() -> {
            try {
                api.show(label);
                labels.put(key, label);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
