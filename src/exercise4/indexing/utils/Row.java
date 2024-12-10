package exercise4.indexing.utils;

import java.util.Arrays;

public class Row {
    private final Object[] columns;

    public Row(Object... columns) {
        this.columns = columns;
    }

    Object getColumn(int index) {
        return this.columns[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(this.columns);
    }
}

