package stocks;

import util.records.RecordsBuffer;
import util.records.StocksBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Stocks implements Iterable<StockEntry> {

    private final RecordsBuffer stocksBuffer;

    Stocks(String path) throws FileNotFoundException {
        RandomAccessFile file = new RandomAccessFile(path, "r");
        stocksBuffer = new StocksBuffer(file);
    }

    public StockEntry get(int i) {
        try {
            ByteBuffer byteBuffer = stocksBuffer.getRecordNumbered(i);
            return new StockEntry(byteBuffer);
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterator<StockEntry> iterator() {
        return new StockEntryIterator(stocksBuffer);
    }
}
