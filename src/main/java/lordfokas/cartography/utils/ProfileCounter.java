package lordfokas.cartography.utils;

import java.util.HashMap;
import java.util.Map;

public class ProfileCounter {
    private String dominant = null;
    private final HashMap<String, Integer> names = new HashMap<>();
    private final HashMap<TFCBlockTypes.Profile, Integer> profiles = new HashMap<>();

    public void consume(TFCBlockTypes.Profile[][] profileMatrix) {
        names.clear();
        profiles.clear();
        dominant = null;
        for(TFCBlockTypes.Profile[] profileArray : profileMatrix) {
            for(TFCBlockTypes.Profile profile : profileArray) {
                if(profile != null) {
                    profiles.put(profile, profiles.computeIfAbsent(profile, $ -> 0) + 1);
                    names.put(profile.name, names.computeIfAbsent(profile.name, $ -> 0) + 1);
                }
            }
        }

        if(names.size() > 0) {
            int max = 0;
            for(Map.Entry<String, Integer> entry : names.entrySet()) {
                if(entry.getValue() > max) {
                    max = entry.getValue();
                    dominant = entry.getKey();
                }
            }
        }
    }

    public void consume(TFCBlockTypes.Profile[] profileArray) {
        names.clear();
        profiles.clear();
        dominant = null;
        for(TFCBlockTypes.Profile profile : profileArray) {
            if(profile != null) {
                profiles.put(profile, profiles.computeIfAbsent(profile, $ -> 0) + 1);
                names.put(profile.name, names.computeIfAbsent(profile.name, $ -> 0) + 1);
            }
        }

        if(names.size() > 0) {
            int max = 0;
            for(Map.Entry<String, Integer> entry : names.entrySet()) {
                if(entry.getValue() > max) {
                    max = entry.getValue();
                    dominant = entry.getKey();
                }
            }
        }
    }

    public String getDominantName() {
        return dominant;
    }

    public TFCBlockTypes.Profile getDominantProfile() {
        if(dominant == null) return null;
        var ref = new Object() {
            int count = -1;
            TFCBlockTypes.Profile result = null;
        };

        profiles.entrySet().stream().filter(e -> e.getKey().name.equals(dominant)).forEach(e -> {
            int curr = e.getValue();
            if(curr > ref.count) {
                ref.count = curr;
                ref.result = e.getKey();
            }
        });
        return ref.result;
    }
}
