package lordfokas.cartography.feature.environment.forest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import com.eerussianguy.blazemap.api.util.RegionPos;
import com.eerussianguy.blazemap.engine.BlazeMapAsync;
import com.eerussianguy.blazemap.engine.async.DebouncingDomain;
import lordfokas.cartography.data.IClusterConsumer;

public class ForestRegion {
    private static final ThreadLocal<Forest.ChangeBatch> BATCHES = ThreadLocal.withInitial(Forest.ChangeBatch::new);
    private static final DebouncingDomain<ForestRegion> DEBOUNCER = new DebouncingDomain<>(BlazeMapAsync.instance().debouncer, ForestRegion::save, 5_000, 30_000);
    private final Map<String, Forest> forests = new HashMap<>();
    private final IClusterConsumer<Forest> consumer;
    private final ForestDataPool pool;
    private final RegionPos region;

    public ForestRegion(RegionPos region, IClusterConsumer<Forest> consumer, ForestDataPool pool) {
        this.consumer = consumer;
        this.region = region;
        this.pool = pool;
    }

    public ForestRegion(RegionPos region, IClusterConsumer<Forest> consumer, ForestDataPool pool, HashMap<String, Integer> data) {
        this(region, consumer, pool);
        for(var entry : data.entrySet()) {
            Forest forest = getForest(entry.getKey());
            forest.change(entry.getValue());
            consumer.pushCluster(forest);
        }
    }

    public void change(Consumer<Forest.ChangeBatch> consumer) {
        Forest.ChangeBatch batch = BATCHES.get().with(forests, this::getForest, this.consumer);
        synchronized(forests) {
            batch.begin();
            consumer.accept(batch);
            batch.end();
            index();
            DEBOUNCER.push(this);
        }
    }

    private void index() {
        int total = forests.size();
        int index = 0;
        for(Iterator<Forest> it = forests.values().stream().sorted((f1, f2) -> f2.size() - f1.size()).iterator(); it.hasNext(); ) {
            Forest forest = it.next();
            forest.total = total;
            forest.index = index++;
        }
    }

    private void save() {
        HashMap<String, Integer> data = new HashMap<>();
        synchronized(forests) {
            for(var entry : forests.entrySet()) {
                data.put(entry.getKey(), entry.getValue().size());
            }
        }
        pool.addData(region, data);
    }

    private Forest getForest(String tree) {
        return forests.computeIfAbsent(tree, t -> new Forest(t, region));
    }
}
