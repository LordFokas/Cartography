package lordfokas.cartography.utils;

import java.util.HashMap;
import java.util.Map;

import lordfokas.cartography.feature.TFCContent;

public class ProfileCounter {
    private String dominant = null;
    private final HashMap<String, Integer> names = new HashMap<>();
    private final HashMap<TFCContent.Profile, Integer> profiles = new HashMap<>();

    public void consume(TFCContent.Profile[][] profileMatrix) {
        names.clear();
        profiles.clear();
        dominant = null;
        for(TFCContent.Profile[] profileArray : profileMatrix) {
            for(TFCContent.Profile profile : profileArray) {
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

    public void consume(TFCContent.Profile[] profileArray) {
        names.clear();
        profiles.clear();
        dominant = null;
        for(TFCContent.Profile profile : profileArray) {
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

    public TFCContent.Profile getDominantProfile() {
        if(dominant == null) return null;
        var ref = new Object() {
            int count = -1;
            TFCContent.Profile result = null;
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
