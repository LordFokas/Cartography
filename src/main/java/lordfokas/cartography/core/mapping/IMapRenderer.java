package lordfokas.cartography.core.mapping;

import lordfokas.cartography.core.markers.IMarkerHandler;

import java.awt.image.BufferedImage;

public interface IMapRenderer {
    boolean render(BufferedImage image, IChunkData chunk, IMarkerHandler markers);
}
