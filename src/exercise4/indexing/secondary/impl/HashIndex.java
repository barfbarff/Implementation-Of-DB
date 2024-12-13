package exercise4.indexing.secondary.impl;


import exercise4.indexing.secondary.SecondaryIndex;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of a SecondaryIndex around a HashMap.
 */
public class HashIndex implements SecondaryIndex {
    private final Map<Object, Set<Long>> index;

    public HashIndex() {
        this.index = new HashMap<>();
    }

    @Override
    public void insert(Object value, Long tid) {
        index.computeIfAbsent(value, k -> new HashSet<>()).add(tid);
    }

    @Override
    public List<Long> get(Object value) {
        return new ArrayList<>(index.getOrDefault(value, Collections.emptySet()));
    }

    @Override
    public boolean remove(Long tid, Object value) {
        Set<Long> tids = index.get(value);

        if (tids != null && tids.remove(tid)) {
            if (tids.isEmpty()) {
                index.remove(value);
            }
            return true;
        }

        return false;
    }
}