package lordfokas.cartography.modules.biology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TreeCounter {
    private final HashMap<String, Counter> counters = new HashMap<>();

    public void add(Iterable<ITreeDataHandler.TreeSummary> summaries){
        for(ITreeDataHandler.TreeSummary summary : summaries){
            add(summary);
        }
    }

    public void add(ITreeDataHandler.TreeSummary summary){
        counters.computeIfAbsent(summary.tree, t -> new Counter()).value += summary.count;
    }

    public void increment(String tree){
        counters.computeIfAbsent(tree, t -> new Counter()).value++;
    }

    public void remove(Iterable<ITreeDataHandler.TreeSummary> summaries){
        for(ITreeDataHandler.TreeSummary summary : summaries){
            remove(summary);
        }
    }

    public void remove(ITreeDataHandler.TreeSummary summary){
        Counter counter = counters.computeIfAbsent(summary.tree, t -> new Counter());
        counter.value -= summary.count;
        if(counter.value < 1) counters.remove(summary.tree);
    }

    public Collection<ITreeDataHandler.TreeSummary> summarize(){
        return counters.entrySet()
            .stream()
            .map(e -> new ITreeDataHandler.TreeSummary(e.getKey(), e.getValue().value))
            .collect(Collectors.toCollection(() -> new ArrayList<>(8)));
    }

    private static class Counter { int value; }
}
