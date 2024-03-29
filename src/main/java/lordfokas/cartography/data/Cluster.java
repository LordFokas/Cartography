package lordfokas.cartography.data;

import java.util.Collection;

public class Cluster<C, D> {
    private final Collection<C> coordinates;
    protected final D data;

    public Cluster(Collection<C> coordinates, D data) {
        this.coordinates = coordinates;
        this.data = data;
    }

    public Collection<C> getCoordinates() {
        return coordinates;
    }

    public D getData() {
        return data;
    }
}
