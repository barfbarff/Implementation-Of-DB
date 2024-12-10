package exercise4.indexing.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Schema {
    public enum Type {
        LONG,
        STRING,
        INTEGER,
        DOUBLE,
        BLOB
    }

    private final List<Comparator<Object>> comps;

    /**
     * Returns the comparator of column index `column`.
     * Returns null, if the column doesn't support ordering.
     */
    public Comparator<Object> getComparatorOfColumn(int column) {
        return this.comps.get(column);
    }

    public Schema(Type... types) {
        this.comps = new ArrayList<>();
        for (Type type : types) {
            switch (type) {
                case LONG:
                    this.comps.add(Comparator.comparing(o -> ((Long) o)));
                    break;
                case STRING:
                    this.comps.add(Comparator.comparing(o -> ((String) o)));
                    break;
                case INTEGER:
                    this.comps.add(Comparator.comparing(o -> ((Integer) o)));
                    break;
                case DOUBLE:
                    this.comps.add(Comparator.comparing(o -> ((Double) o)));
                    break;
                case BLOB:
                    this.comps.add(null);
            }
        }
    }

    public int columnCount() {
        return this.comps.size();
    }
}