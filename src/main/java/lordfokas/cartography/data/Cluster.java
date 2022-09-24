package lordfokas.cartography.data;

import java.util.Collection;

public class Cluster<C, D, R>{
    private final Collection<C> coordinates;
    private D data;
    private final Collection<R> keys;

    public Cluster(Collection<C> coordinates, D data, Collection<R> keys){
        this.coordinates = coordinates;
        this.data = data;
        this.keys = keys;
    }

    public Collection<C> getCoordinates(){
        return coordinates;
    }

    public void setData(D data){
        this.data = data;
    }

    public D getData(){
        return data;
    }

    public Collection<R> getKeys(){
        return keys;
    }
}
