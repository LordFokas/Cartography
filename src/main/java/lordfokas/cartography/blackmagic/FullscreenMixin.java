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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = Fullscreen.class, remap = false)
public abstract class FullscreenMixin extends JmUI {
    @Shadow @Final private static final MapState state = new MapState();
    @Shadow private ThemeButton buttonDay;
    @Shadow private ThemeButton buttonNight;
    @Shadow private ThemeButton buttonTopo;
    @Shadow private ThemeButton buttonLayers;
    @Shadow private ThemeToolbar mapTypeToolbar;
    protected ThemeButton isohyetal, isothermal, geological;

    private FullscreenMixin(String title, Void __) { super(title); }

    @Inject(method = "initButtons()V", at = @At("RETURN"))
    private void interceptInitButtons(CallbackInfo ci){
        if(this.isohyetal == null){
            Theme _theme = ThemeLoader.getCurrentTheme();
            MapType _type = state.getMapType();

            this.isohyetal = this.addButton(new ThemeToggle(_theme, "cartography.fullscreen.map_isohyetal", "isohyetal", (button) -> {
                if (this.isohyetal.isEnabled()) {
                    this.updateMapType(new MapType(JMHacks.ISOHYETAL, null, state.getDimension()));
                }
            }));
            this.isohyetal.setDrawButton(true);
            this.isohyetal.setToggled(_type.name == JMHacks.ISOHYETAL, false);
            this.isohyetal.setStaysOn(true);


            this.isothermal = this.addButton(new ThemeToggle(_theme, "cartography.fullscreen.map_isothermal", "isothermal", (button) -> {
                if (this.isothermal.isEnabled()) {
                    this.updateMapType(new MapType(JMHacks.ISOTHERMAL, null, state.getDimension()));
                }
            }));
            this.isothermal.setDrawButton(true);
            this.isothermal.setToggled(_type.name == JMHacks.ISOTHERMAL, false);
            this.isothermal.setStaysOn(true);


            this.geological = this.addButton(new ThemeToggle(_theme, "cartography.fullscreen.map_geological", "geological", (button) -> {
                if (this.geological.isEnabled()) {
                    this.updateMapType(new MapType(JMHacks.GEOLOGICAL, null, state.getDimension()));
                }
            }));
            this.geological.setDrawButton(true);
            this.geological.setToggled(_type.name == JMHacks.GEOLOGICAL, false);
            this.geological.setStaysOn(true);


            this.getButtonList().remove(this.mapTypeToolbar);
            this.mapTypeToolbar = new ThemeToolbar(_theme, this.buttonLayers, this.buttonTopo, this.buttonNight, this.buttonDay, this.isohyetal, this.isothermal, this.geological);
            this.mapTypeToolbar.addAllButtons(this);
        }
    }

    @Shadow private void updateMapType(MapType type){}

    @Inject(method = "updateMapType(Ljourneymap/client/model/MapType;)V", at = @At("HEAD"))
    private void interceptUpdateMapType(MapType type, CallbackInfo ci){
        if (!type.isAllowed()) {
            type = state.getMapType();
        }

        isohyetal.setToggled(type.name == JMHacks.ISOHYETAL, false);
        isothermal.setToggled(type.name == JMHacks.ISOTHERMAL, false);
        geological.setToggled(type.name == JMHacks.GEOLOGICAL, false);
    }
}
