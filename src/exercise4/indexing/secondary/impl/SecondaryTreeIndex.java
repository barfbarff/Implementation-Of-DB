package exercise4.indexing.secondary.impl;


import exercise4.indexing.secondary.SecondaryIndex;

import java.util.*;

/**
 * An implementation of a SecondaryIndex around a TreeMap.
 */
public class SecondaryTreeIndex implements SecondaryIndex {
    private final TreeMap<Object, Set<Long>> treeIndex;

    public SecondaryTreeIndex(Comparator<Object> cmp) {
        this.treeIndex = new TreeMap<>(cmp);
    }

    @Override
    public void insert(Object value, Long tid) {
        treeIndex.computeIfAbsent(value, k -> new HashSet<>()).add(tid);
    }

    @Override
    public List<Long> get(Object value) {
        return new ArrayList<>(treeIndex.getOrDefault(value, Collections.emptySet()));
    }

    @Override
    public boolean remove(Long tid, Object value) {
        Set<Long> tids = treeIndex.get(value);

        if (tids != null && tids.remove(tid)) {
            if (tids.isEmpty()) {
                treeIndex.remove(value);
            }
            return true;
        }

        return false;
    }

    @Override
    public List<Long> getRange(Object from, Object to) {
        return treeIndex.subMap(from, to).values().stream()
                .flatMap(Set::stream)
                .toList();
    }
}
