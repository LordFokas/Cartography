package lordfokas.cartography.core.mapping.continuous;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;

public interface IContinuousDataSource {
    DataType getDataType();
    ContinuousDatum getDatum(IChunkData chunk, int x, int y);
}
