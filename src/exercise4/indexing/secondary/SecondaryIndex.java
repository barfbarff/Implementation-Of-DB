package exercise4.indexing.secondary;

import java.util.List;

public interface SecondaryIndex {
    /**
     * Insert the pair (value, tid) into the index.
     */
    void insert(Object value, Long tid);

    /**
     * Perform a point query on the index.
     * Returns a List of tuple identifiers of rows where the indexed column value equals the given value.
     */
    List<Long> get(Object value);

    /**
     * Remove the row with identifier tid and the respective value from the index.
     * Returns true if the row was removed, false otherwise.
     */
    boolean remove(Long tid, Object value);

    /**
     * Perform a range query on the index, if supported.
     * Returns a stream of tuple identifiers of rows where the indexed value is in the interval [from, to).
     * Must return null if the index does not support range queries.
     */
    default List<Long> getRange(Object from, Object to) {
        return null;
    }
}