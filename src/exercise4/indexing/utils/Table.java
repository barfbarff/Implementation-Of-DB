package exercise4.indexing.utils;


import exercise4.indexing.primary.PrimaryIndex;
import exercise4.indexing.secondary.SecondaryIndex;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table {
    private final Schema schema;
    private final PrimaryIndex primaryIndex;
    private final SecondaryIndex[] secondaryIndexes;

    public Table(Schema schema, PrimaryIndex primaryIndex) {
        this.schema = schema;
        this.primaryIndex = primaryIndex;
        this.secondaryIndexes = new SecondaryIndex[schema.columnCount()];
    }

    /**
     * Set the secondary index for the respective column, replacing any existing index.
     * Rows already in the table must be added to the index.
     */
    public void setSecondaryIndex(int columnIndex, SecondaryIndex index) {
        this.primaryIndex.scan().forEach(entry -> index
                .insert(entry.getValue().getColumn(columnIndex), entry.getKey()));
        this.secondaryIndexes[columnIndex] = index;
    }

    /**
     * Insert a row into the table by assigning a new TID and adding it to all primary/secondary indexes.
     */
    public void insert(Row row) {
        Long tid = primaryIndex.insert(row);

        for (int i = 0; i < schema.columnCount(); i++) {
            if (secondaryIndexes[i] != null) {
                secondaryIndexes[i].insert(row.getColumn(i), tid);
            }
        }
    }

    public boolean remove(ResultSet resultSet) {
        boolean removedElement = false;

        for (long tid : resultSet.getTids()) {
            Row row = primaryIndex.get(tid);

            if (row != null) {
                primaryIndex.remove(tid);

                for (int i = 0; i < schema.columnCount(); i++) {
                    if (secondaryIndexes[i] != null) {
                        secondaryIndexes[i].remove(tid, row.getColumn(i));
                    }
                }

                removedElement = true;
            }
        }

        return removedElement;
    }

    /**
     * Perform a point query on the table. Returns the ResultSet of all rows with the respective column value at the given column.
     */
    public ResultSet pointQueryAtColumn(int columnIndex, Object value) {
        if (secondaryIndexes[columnIndex] != null) {
            List<Long> tids = secondaryIndexes[columnIndex].get(value);
            return new ResultSet(primaryIndex, new HashSet<>(tids));
        }

        Set<Long> matchingTids = new HashSet<>();
        primaryIndex.scan().forEach(entry -> {
            Row row = entry.getValue();
            if (value.equals(row.getColumn(columnIndex))) {
                matchingTids.add(entry.getKey());
            }
        });

        return new ResultSet(primaryIndex, matchingTids);
    }

    /**
     * Perform a range query on the table. Returns the ResultSet of all rows with column value at columnIndex in the interval [from, to).
     */
    public ResultSet rangeQueryAtColumn(int columnIndex, Object from, Object to) {
        Comparator<Object> columnComparator = schema.getComparatorOfColumn(columnIndex);
        if (columnComparator == null) {
            throw new UnsupportedOperationException("The column " + columnIndex + " does not have comparator");
        }

        if (secondaryIndexes[columnIndex] != null) {
            List<Long> tids = secondaryIndexes[columnIndex].getRange(from, to);
            if (tids != null) {
                return new ResultSet(primaryIndex, new HashSet<>(tids));
            }
        }

        Set<Long> matchingTids = new HashSet<>();
        primaryIndex.scan().forEach(entry -> {
            Row row = entry.getValue();
            Object columnValue = row.getColumn(columnIndex);
            if (columnComparator.compare(columnValue, from) >= 0 && columnComparator.compare(columnValue, to) < 0) {
                matchingTids.add(entry.getKey());
            }
        });

        return new ResultSet(primaryIndex, matchingTids);
    }
}