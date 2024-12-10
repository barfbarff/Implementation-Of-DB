package exercise4.indexing.secondary.impl;


import exercise4.indexing.secondary.SecondaryIndex;


import java.util.List;

/**
 * An implementation of a SecondaryIndex around a HashMap.
 */
public class HashIndex implements SecondaryIndex {
    // TODO: add field(s) as necessary

    public HashIndex() {
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
}