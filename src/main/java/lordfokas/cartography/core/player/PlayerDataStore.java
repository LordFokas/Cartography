package lordfokas.cartography.core.player;

import java.io.*;

public class PlayerDataStore implements IPlayerDataStore {
    private final File dir;

    public PlayerDataStore(File dir){
        this.dir = dir;
    }

    @Override
    public boolean isAbsent(String node) {
        return !getFile(node).exists();
    }

    @Override
    public DataInputStream getInputStream(String node) throws IOException {
        return new DataInputStream(new FileInputStream(getFile(node)));
    }

    @Override
    public DataOutputStream getOutputStream(String node) throws IOException {
        return new DataOutputStream(new FileOutputStream(getFile(node)));
    }

    private File getFile(String node){
        return new File(dir, node);
    }
}
