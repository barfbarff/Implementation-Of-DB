package exercise4.indexing;

import exercise4.indexing.primary.impl.PrimaryTreeIndex;
import exercise4.indexing.secondary.impl.SecondaryTreeIndex;
import exercise4.indexing.utils.Row;
import exercise4.indexing.utils.Schema;
import exercise4.indexing.utils.Table;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Schema schema = new Schema(Schema.Type.LONG, Schema.Type.STRING);
        Table table = new Table(schema, new PrimaryTreeIndex());

        // Create ordered index on the first column
        table.setSecondaryIndex(0, new SecondaryTreeIndex(schema.getComparatorOfColumn(0)));
        table.setSecondaryIndex(1, new SecondaryTreeIndex(schema.getComparatorOfColumn(1)));

        // Fill the table with some random data
        Random rng = new Random();
        byte[] buf = new byte[8];

        for (int i = 0; i < 2_000; i++) {
            for (int j = 0; j < buf.length; j++)
                buf[j] = (byte) ('A' + rng.nextInt(26));

            table.insert(new Row(
                    (long) i,
                    new String(buf)
            ));
        }

        System.out.println("SELECT * FROM Table " +
                "WHERE (col0 < 100 AND col1 like 'A%') OR col0 BETWEEN 1000 AND 105;");
        // TODO: impl. and run the query and print the output
    }
}