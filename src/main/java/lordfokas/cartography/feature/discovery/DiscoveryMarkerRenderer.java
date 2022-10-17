package lordfokas.cartography.feature.discovery;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.markers.ObjectRenderer;
import com.eerussianguy.blazemap.api.markers.SearchTargeting;
import com.eerussianguy.blazemap.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.ImageHandler;

public class DiscoveryMarkerRenderer implements ObjectRenderer<DiscoveryMarker> {
    public static final ResourceLocation DOT = Cartography.resource("icons/dot.png");
    public static final ResourceLocation LABEL = Cartography.resource("textures/label.png");

    @Override
    public void render(DiscoveryMarker marker, PoseStack stack, MultiBufferSource buffers, double zoom, SearchTargeting search) {
        VertexConsumer dots = buffers.getBuffer(RenderType.text(DOT));
        int color = ImageHandler.getColor(marker.getIcon());
        for(int idx = 0; idx < marker.offsets.length; idx += 2){
            stack.pushPose();
            stack.translate(marker.offsets[idx]-3, marker.offsets[idx+1]-3, 0);
            RenderHelper.drawQuad(dots, stack.last().pose(), 6F, 6F, search.color(color));
            stack.popPose();
        }

        int width = marker.getWidth();
        int height = marker.getHeight();
        stack.translate(-width / 2, -height / 2, 1.0D);
        VertexConsumer icon = buffers.getBuffer(RenderType.text(marker.getIcon()));
        RenderHelper.drawQuad(icon, stack.last().pose(), (float) width, (float) height, search.color());

        if(search == SearchTargeting.HIT){
            Font font = Minecraft.getInstance().font;
            String label = "Furfles";
            stack.translate(width + 2, 0, 0);
            RenderHelper.drawFrame(buffers.getBuffer(RenderType.text(LABEL)), stack, font.width(label)+8, marker.getHeight(), 4);
            stack.translate(4, (width - font.lineHeight) / 2, 0);
            font.drawInBatch(label, 0, 0, search.color(), false, stack.last().pose(), buffers, false, 0, LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public BlazeRegistry.Key<ObjectRenderer<?>> getID() {
        return CartographyReferences.Renderers.DISCOVERY_MARKER;
    }
}
