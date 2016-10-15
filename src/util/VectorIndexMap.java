package util;

import java.util.HashMap;

/**
 * Created by iVerb on 26-5-2015.
 */
public class VectorIndexMap<T> extends HashMap<Integer, T> {

    private int idGen = 0;

    public void add(T t) {
        put(idGen++, t);
    }

    public void add(T... array) {
        for (T t : array) {
            add(t);
        }
    }

    public void clear() {
        idGen = 0;
    }

}
