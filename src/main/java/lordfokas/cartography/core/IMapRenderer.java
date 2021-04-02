package lordfokas.cartography.core;

import java.awt.image.BufferedImage;

public interface IMapRenderer {
    boolean render(BufferedImage image, IChunkData chunk);
}
