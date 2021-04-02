package lordfokas.cartography.core.continuous;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.IChunkData;

public interface IContinuousDataSource {
    DataType getDataType();
    ContinuousDatum getDatum(IChunkData chunk, int x, int y);
}
