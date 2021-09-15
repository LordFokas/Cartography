package lordfokas.cartography.blackmagic;

import journeymap.client.io.ThemeLoader;
import journeymap.client.model.MapState;
import journeymap.client.model.MapType;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeButton;
import journeymap.client.ui.theme.ThemeToggle;
import journeymap.client.ui.theme.ThemeToolbar;
import lordfokas.cartography.integration.journeymap.JMHacks;
import lordfokas.cartography.utils.Pointer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;


@Mixin(value = Fullscreen.class, remap = false)
public abstract class FullscreenMixin extends JmUI {
    @Shadow @Final private static final MapState state = new MapState();
    @Shadow private ThemeButton buttonDay;
    @Shadow private ThemeButton buttonNight;
    @Shadow private ThemeButton buttonTopo;
    @Shadow private ThemeButton buttonLayers;
    @Shadow private ThemeToolbar mapTypeToolbar;

    protected final HashMap<MapType.Name, ThemeButton> buttonMap = new HashMap<>();
    protected int size = -1;

    private FullscreenMixin(String title, Void __) { super(title); }

    @Inject(method = "initButtons()V", at = @At("RETURN"))
    private void interceptInitButtons(CallbackInfo ci){
        if(this.buttonMap.size() == 0 || this.getButtonList().size() <= size){
            size = this.getButtonList().size();
            Theme _theme = ThemeLoader.getCurrentTheme();
            MapType _type = state.getMapType();

            for(MapType.Name name : JMHacks.getCustomNames()){
                Pointer<ThemeButton> pointer = new Pointer<>();
                ThemeButton button = pointer.value = this.addRenderableWidget(new ThemeToggle(_theme, "cartography.fullscreen.map_"+name.name(), name.name(), (b) -> {
                    if (pointer.value.isEnabled()) {
                        this.updateMapType(new MapType(name, null, state.getDimension()));
                    }
                }));
                button.setDrawButton(true);
                button.setToggled(_type.name == name, false);
                button.setStaysOn(true);
                buttonMap.put(name, button);
            }

            ArrayList<ThemeButton> list = new ArrayList<>(4 + buttonMap.size());
            list.add(this.buttonLayers);
            list.add(this.buttonTopo);
            list.add(this.buttonNight);
            list.add(this.buttonDay);
            for(MapType.Name name : JMHacks.getCustomNames()) { // make sure buttons are ordered
                list.add(buttonMap.get(name));
            }
            this.getButtonList().remove(this.mapTypeToolbar);
            this.mapTypeToolbar = new ThemeToolbar(_theme, list.toArray(new ThemeButton[]{}));
            this.mapTypeToolbar.addAllButtons(this);
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Shadow private void updateMapType(MapType type){}

    @Inject(method = "updateMapType(Ljourneymap/client/model/MapType;)V", at = @At("HEAD"))
    private void interceptUpdateMapType(MapType type, CallbackInfo ci){
        if(!type.isAllowed())
            type = state.getMapType();
        final MapType.Name _name = type.name;
        buttonMap.forEach((name, button) -> button.setToggled(_name == name, false));
    }
}
