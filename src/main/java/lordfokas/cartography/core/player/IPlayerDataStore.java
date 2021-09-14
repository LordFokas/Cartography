package lordfokas.cartography.core.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IPlayerDataStore {
    boolean isAbsent(String node);

    DataInputStream getInputStream(String node) throws IOException;
    DataOutputStream getOutputStream(String node) throws IOException;
}
