package lordfokas.cartography.feature.discovery;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.api.markers.ObjectRenderer;
import com.eerussianguy.blazemap.api.markers.SearchTargeting;
import com.eerussianguy.blazemap.feature.maps.DefaultObjectRenderer;
import com.eerussianguy.blazemap.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;

public class DiscoveryMarkerRenderer implements ObjectRenderer<DiscoveryMarker> {
    public static final ResourceLocation DOT = Cartography.resource("icons/dot.png");
    public static final ObjectRenderer<MapLabel> RENDERER = new DefaultObjectRenderer();

    @Override
    public void render(DiscoveryMarker marker, PoseStack stack, MultiBufferSource buffers, double zoom, SearchTargeting search) {
        VertexConsumer dots = buffers.getBuffer(RenderType.text(DOT));
        int color = ImageHandler.getColor(marker.item);
        if(marker.depleted) color = Colors.withAlpha(color, (byte) 0x80);
        for(int idx = 0; idx < marker.offsets.length; idx += 2){
            stack.pushPose();
            stack.translate(marker.offsets[idx]-3, marker.offsets[idx+1]-3, 0);
            RenderHelper.drawQuad(dots, stack.last().pose(), 6F, 6F, color);
            stack.popPose();
        }

        stack.pushPose();
        if(search == SearchTargeting.HIT) {
            stack.translate(0, 0, 5);
            RENDERER.render(marker, stack, buffers, zoom, search);
        } else {
            stack.translate(-8, -8, 2);
            VertexConsumer icon = buffers.getBuffer(RenderType.text(marker.item));
            RenderHelper.drawQuad(icon, stack.last().pose(), 16, 16, 0x80FFFFFF);
        }
        stack.popPose();
    }

    @Override
    public BlazeRegistry.Key<ObjectRenderer<?>> getID() {
        return CartographyReferences.Renderers.DISCOVERY_MARKER;
    }
}
