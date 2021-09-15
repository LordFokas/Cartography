package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.data.DataFlow;
import net.minecraft.world.level.ChunkPos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TreeDataSerializer implements DataFlow.IDataSerializer<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> {

    @Override
    public void deserialize(DataInputStream stream, DataFlow.IDataSink<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> sink) throws IOException {
        HashMap<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> data = new HashMap<>();
        int regions = stream.read();
        for(int i = 0; i < regions; i++){
            ChunkPos pos = new ChunkPos(stream.read(), stream.read());
            int records = stream.read();
            Collection<ITreeDataHandler.TreeSummary> summaries = new ArrayList<>(records);
            for(int j = 0; j < records; j++){
                summaries.add(new ITreeDataHandler.TreeSummary(stream.readUTF(), stream.read()));
            }
            data.put(pos, summaries);
        }
        sink.setData(data);
    }

    @Override
    public void serialize(DataOutputStream stream, DataFlow.IDataSource<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> source) throws IOException {
        Collection<ChunkPos> keys = source.keys();
        stream.write(keys.size());
        for(ChunkPos pos : keys){
            Collection<ITreeDataHandler.TreeSummary> summaries = source.get(pos);
            stream.write(pos.x);
            stream.write(pos.z);
            stream.write(summaries.size());
            for(ITreeDataHandler.TreeSummary summary : summaries){
                stream.writeUTF(summary.tree);
                stream.write(summary.count);
            }
        }
    }
}
