package lordfokas.cartography.data;

import java.util.Collection;

public class Cluster<C, D> {
    private final Collection<C> coordinates;
    private D data;

    public Cluster(Collection<C> coordinates, D data) {
        this.coordinates = coordinates;
        this.data = data;
    }

    public Collection<C> getCoordinates() {
        return coordinates;
    }

    public void setData(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }
}
