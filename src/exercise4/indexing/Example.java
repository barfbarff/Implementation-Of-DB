package exercise4.indexing;

import exercise4.indexing.primary.impl.PrimaryTreeIndex;
import exercise4.indexing.secondary.impl.HashIndex;
import exercise4.indexing.secondary.impl.SecondaryTreeIndex;
import exercise4.indexing.utils.ResultSet;
import exercise4.indexing.utils.Row;
import exercise4.indexing.utils.Schema;
import exercise4.indexing.utils.Table;

import java.util.Random;

public class Example {
    public static void main(String[] args) {
        Schema schema = new Schema(Schema.Type.LONG, Schema.Type.STRING);
        Table table = new Table(schema, new PrimaryTreeIndex());

        // create ordered index on the first column
        table.setSecondaryIndex(0, new SecondaryTreeIndex(schema.getComparatorOfColumn(0)));

        // Populate table with random data
        Random rng = new Random();
        byte[] buf = new byte[8];

        for (int i = 0; i < 1_000_000; i++) {
            for (int j = 0; j < buf.length; j++)
                buf[j] = (byte) ('A' + rng.nextInt(26));

            table.insert(new Row(
                    (long) i,
                    new String(buf)
            ));
        }

        // Point query on the first column. Note that the queried value must be a long, not an int.
        ResultSet res = table.pointQueryAtColumn(0, 50L);
        res.stream().forEach(System.out::println);

        // Delete the result from the table
        table.remove(res);

        // Row should not be found now
        table.pointQueryAtColumn(0, 50L).stream().forEach(System.out::println);

        // Range query on the first column
        table.rangeQueryAtColumn(0, 0L, 8L).stream().forEach(System.out::println);

        // Range query on the string column: all rows with prefix "AAA" (why?)
        table.rangeQueryAtColumn(1, "AAA", "AAB").stream().forEach(System.out::println);

        // Some simple benchmarking:
        long t0, t1;
        t0 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            table.pointQueryAtColumn(1, "ABCDEFGHI");
        }
        t1 = System.currentTimeMillis();
        System.out.println("PointQuery: " + (t1 - t0) * 1.0 / 10 * 1000 + "us");

        // Create ordered index on the second column
        table.setSecondaryIndex(1, new SecondaryTreeIndex(schema.getComparatorOfColumn(1)));
        t0 = System.currentTimeMillis();
        for (int i = 0; i < 10_000_000; i++) {
            table.pointQueryAtColumn(1, "ABCDEFGHI");
        }
        t1 = System.currentTimeMillis();
        System.out.println("PointQuery, Tree: " + (t1 - t0) * 1.0 / 10_000_000 * 1000 + "us");

        // Replace index by a hash index, measure point query again
        table.setSecondaryIndex(1, new HashIndex());
        t0 = System.currentTimeMillis();
        for (int i = 0; i < 10_000_000; i++) {
            table.pointQueryAtColumn(1, "ABCDEFGHI");
        }
        t1 = System.currentTimeMillis();
        System.out.println("PointQuery, Hash: " + (t1 - t0) * 1.0 / 10_000_000 * 1000 + "us");
    }
}