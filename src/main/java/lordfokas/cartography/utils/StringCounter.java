package lordfokas.cartography.utils;

import java.util.HashMap;
import java.util.Map;

public class StringCounter {
    private String dominant = null;
    private final HashMap<String, Integer> counters = new HashMap<>();

    public void consume(TFCBlockTypes.Profile[][] profileMatrix){
        counters.clear();
        dominant = null;
        for(TFCBlockTypes.Profile[] profileArray : profileMatrix) {
            for(TFCBlockTypes.Profile profile : profileArray) {
                if(profile != null) {
                    int count = counters.computeIfAbsent(profile.name, $ -> 0);
                    counters.put(profile.name, count + 1);
                }
            }
        }

        if(counters.size() > 0){
            int max = 0;
            for(Map.Entry<String, Integer> entry : counters.entrySet()){
                if(entry.getValue() > max){
                    max = entry.getValue();
                    dominant = entry.getKey();
                }
            }
        }
    }

    public String getDominant(){
        return dominant;
    }
}
