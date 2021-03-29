package lordfokas.cartography.integration.journeymap.continuous;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;

public interface IContinuousDataSource {
    DataType getDataType();
    ContinuousDatum getDatum(IChunkData chunk, int x, int y);
}
