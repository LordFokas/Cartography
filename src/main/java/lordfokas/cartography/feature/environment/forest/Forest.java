package lordfokas.cartography.feature.environment.forest;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.data.IClusterConsumer;

public class Forest {
    public final String key, tree, pretty;
    public final RegionPos region;
    private int size = 0, previousSize = -1;
    int index, total;

    public Forest(String tree, RegionPos region) {
        this.key = region.x + "_" + region.z + "/" + tree;
        this.tree = tree;
        this.pretty = pretty(tree);
        this.region = region;
    }

    public int size() {
        return size;
    }

    void change(int change) {
        size += change;
    }

    private void beginBatch() {
        previousSize = size;
    }

    private boolean hasChanged() {
        return size != previousSize;
    }

    private boolean isNew() {
        return previousSize == -1;
    }

    private static String pretty(String str) {
        return Arrays.stream(str.split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
    }


    public static class ChangeBatch {
        private Map<String, Forest> forests;
        private Function<String, Forest> allocator;
        private IClusterConsumer<Forest> consumer;

        ChangeBatch with(Map<String, Forest> forests, Function<String, Forest> allocator, IClusterConsumer<Forest> consumer) {
            this.forests = forests;
            this.allocator = allocator;
            this.consumer = consumer;
            return this;
        }

        void begin() {
            forests.values().forEach(Forest::beginBatch);
        }

        void end() {
            forests.values().forEach(forest -> {
                if(forest.size() == 0) {
                    consumer.dropCluster(forest);
                } else if(forest.hasChanged()) {
                    consumer.dropCluster(forest);
                    consumer.pushCluster(forest);
                } else if(forest.isNew()) {
                    consumer.pushCluster(forest);
                }
            });
        }

        public void add(String tree) {
            allocator.apply(tree).change(+1);
        }

        public void remove(String tree) {
            allocator.apply(tree).change(-1);
        }
    }
}
