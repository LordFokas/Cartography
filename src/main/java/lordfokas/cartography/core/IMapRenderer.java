package lordfokas.cartography.core;

import lordfokas.cartography.core.markers.IMarkerPlacer;

import java.awt.image.BufferedImage;

public interface IMapRenderer {
    boolean render(BufferedImage image, IChunkData chunk, IMarkerPlacer labels);
}
