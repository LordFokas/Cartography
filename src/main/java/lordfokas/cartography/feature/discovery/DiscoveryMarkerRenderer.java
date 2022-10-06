package lordfokas.cartography.feature.discovery;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.markers.ObjectRenderer;
import com.eerussianguy.blazemap.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.ImageHandler;

public class DiscoveryMarkerRenderer implements ObjectRenderer<DiscoveryMarker>, BlazeRegistry.RegistryEntry {
    public static final ResourceLocation DOT = Cartography.resource("icons/dot.png");

    @Override
    public void render(DiscoveryMarker marker, PoseStack stack, MultiBufferSource buffers, double zoom) {
        VertexConsumer dots = buffers.getBuffer(RenderType.text(DOT));
        int color = ImageHandler.getColor(marker.getIcon());
        for(int idx = 0; idx < marker.offsets.length; idx += 2){
            stack.pushPose();
            stack.translate(marker.offsets[idx]-3, marker.offsets[idx+1]-3, 0);
            RenderHelper.drawQuad(dots, stack.last().pose(), 6F, 6F, color);
            stack.popPose();
        }

        int width = marker.getWidth();
        int height = marker.getHeight();
        stack.translate(-width / 2, -height / 2, 0.0D);
        VertexConsumer vertices = buffers.getBuffer(RenderType.text(marker.getIcon()));
        RenderHelper.drawQuad(vertices, stack.last().pose(), (float) width, (float) height, -1);
    }

    @Override
    public BlazeRegistry.Key<?> getID() {
        return CartographyReferences.Renderers.DISCOVERY_MARKER;
    }
}
