package exercise4.indexing.primary.impl;

import exercise4.indexing.primary.PrimaryIndex;
import exercise4.indexing.utils.Row;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * An implementation of a primary index around a TreeMap.
 */
public class PrimaryTreeIndex implements PrimaryIndex {
    TreeMap<Long, Row> treeIndex;
    long nextTid;

    public PrimaryTreeIndex() {
        this.nextTid = 0;
        this.treeIndex = new TreeMap<>();
    }

    @Override
    public long insert(Row row) {
        this.treeIndex.put(this.nextTid, row);
        return this.nextTid++;
    }

    @Override
    public Row get(long tid) {
        return this.treeIndex.get(tid);
    }

    @Override
    public Row remove(long tid) {
        return this.treeIndex.remove(tid);
    }

    @Override
    public Stream<Map.Entry<Long, Row>> scan() {
        return this.treeIndex.entrySet().stream();
    }
}