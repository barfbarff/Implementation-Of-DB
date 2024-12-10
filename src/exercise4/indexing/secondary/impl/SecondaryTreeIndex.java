package exercise4.indexing.secondary.impl;


import exercise4.indexing.secondary.SecondaryIndex;

import java.util.*;

/**
 * An implementation of a SecondaryIndex around a TreeMap.
 */
public class SecondaryTreeIndex implements SecondaryIndex {
    // TODO: add field(s) as necessary

    public SecondaryTreeIndex(Comparator<Object> cmp) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(Object value, Long tid) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Long> get(Object value) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Long tid, Object value) {
        // TODO: make sure to only delete the given row/tid
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Long> getRange(Object from, Object to) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
