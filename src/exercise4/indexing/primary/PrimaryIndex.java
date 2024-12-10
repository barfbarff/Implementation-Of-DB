package exercise4.indexing.primary;

import exercise4.indexing.utils.Row;

import java.util.Map;
import java.util.stream.Stream;

public interface PrimaryIndex {
    /**
     * Insert the value into the index and returns the tid.
     */
    long insert(Row row);

    /**
     * Perform a point query on the index.
     * Returns the row of tid.
     */
    Row get(long tid);

    /**
     * Remove the row from the index.
     * Returns the row after removal, null if it was not contained.
     */
    Row remove(long tid);

    /**
     * Returns a stream of all records in the index.
     */
    Stream<Map.Entry<Long, Row>> scan();
}