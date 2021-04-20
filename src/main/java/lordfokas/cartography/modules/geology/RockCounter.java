package lordfokas.cartography.modules.geology;

import java.util.HashMap;
import java.util.Map;

public class RockCounter {
    private final HashMap<String, Counter> counters = new HashMap<>();

    public void increment(String rock){
        counters.computeIfAbsent(rock, t -> new Counter()).value++;
    }

    public String getPredominantRock(){
        String predominant = null;
        int max = 0;

        for(Map.Entry<String, Counter> entry : counters.entrySet()){
            int val = entry.getValue().value;
            if(val > max){
                predominant = entry.getKey();
                max = val;
            }
        }

        return predominant;
    }

    private static class Counter { int value; }
}
