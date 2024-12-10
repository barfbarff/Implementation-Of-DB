package exercise4.indexing.utils;


import exercise4.indexing.primary.PrimaryIndex;

import java.util.Set;
import java.util.stream.Stream;

/**
 * ResultSet represents a query result. Note that the set contains TIDs, not the actual rows.
 * ResultSet::stream() can be used to access the actual rows.
 */
public class ResultSet {
    private final Set<Long> tids;
    private final PrimaryIndex primaryIndex;

    public ResultSet(PrimaryIndex primaryIndex, Set<Long> tids) {
        this.primaryIndex = primaryIndex;
        this.tids = tids;
    }

    public Set<Long> getTids() {
        return this.tids;
    }

    /**
     * Intersect this ResultSet with other.
     */
    public ResultSet intersect(ResultSet other) {
        this.tids.retainAll(other.tids);
        return this;
    }

    /**
     * Calculate the union of this ResultSet with other.
     */
    public ResultSet union(ResultSet other) {
        this.tids.addAll(other.tids);
        return this;
    }

    /**
     * Create a stream over all rows in this ResultSet.
     */
    public Stream<Row> stream() {
        return this.tids.stream().map(this.primaryIndex::get);
    }
}