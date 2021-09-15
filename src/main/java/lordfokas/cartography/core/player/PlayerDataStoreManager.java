package lordfokas.cartography.core.player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDataStoreManager {
    private final HashMap<UUID, HashMap<String, IPlayerDataStore>> CACHE = new HashMap<>();
    private final File base;

    public PlayerDataStoreManager(File base){
        this.base = base;
    }

    public IPlayerDataStore getDataStore(UUID player, String modid){
        return CACHE
            .computeIfAbsent(player, $ -> new HashMap<>())
            .computeIfAbsent(modid,  $ -> new PlayerDataStore(getPlayerModDir(player, modid)));
    }

    private File getPlayerModDir(UUID player, String modid){
        return new File(base, player + File.separator + modid);
    }
}
