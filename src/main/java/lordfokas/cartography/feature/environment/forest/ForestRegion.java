package lordfokas.cartography.feature.environment.forest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.data.IClusterConsumer;

public class ForestRegion {
    private static final ThreadLocal<Forest.ChangeBatch> BATCHES = ThreadLocal.withInitial(Forest.ChangeBatch::new);
    private final Map<String, Forest> forests = new HashMap<>();
    private final IClusterConsumer<Forest> consumer;
    private final RegionPos region;

    public ForestRegion(RegionPos region, IClusterConsumer<Forest> consumer) {
        this.consumer = consumer;
        this.region = region;
    }

    public void change(Consumer<Forest.ChangeBatch> consumer) {
        Forest.ChangeBatch batch = BATCHES.get().with(forests, this::getForest, this.consumer);
        synchronized(forests) {
            batch.begin();
            consumer.accept(batch);
            batch.end();
            index();
        }
    }

    private void index() {
        int total = forests.size();
        int index = 0;
        for(Iterator<Forest> it = forests.values().stream().sorted(Comparator.comparingInt(Forest::size)).iterator(); it.hasNext(); ) {
            Forest forest = it.next();
            forest.total = total;
            forest.index = index++;
        }
    }

    private Forest getForest(String tree) {
        return forests.computeIfAbsent(tree, t -> new Forest(t, region));
    }
}
