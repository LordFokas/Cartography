package lordfokas.cartography.feature.mapping;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.maps.Layer;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.pipeline.MasterDatum;
import lordfokas.cartography.feature.environment.ProfileCounter;

public abstract class CartographyLayer extends Layer {
    protected final ThreadLocal<ProfileCounter> COUNTERS = ThreadLocal.withInitial(ProfileCounter::new);

    @SafeVarargs
    public CartographyLayer(BlazeRegistry.Key<Layer> id, Component name, BlazeRegistry.Key<DataType<MasterDatum>>... inputs) {
        super(id, name, inputs);
    }

    @SafeVarargs
    public CartographyLayer(BlazeRegistry.Key<Layer> id, Component name, ResourceLocation icon, BlazeRegistry.Key<DataType<MasterDatum>>... inputs) {
        super(id, name, icon, inputs);
    }

    @Override
    public boolean shouldRenderInDimension(ResourceKey<Level> dimension) {
        return dimension.equals(Level.OVERWORLD);
    }
}
