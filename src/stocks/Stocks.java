package stocks;

import util.StocksBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Stocks implements Iterable<StockEntry> {

    private final StocksBuffer stocksBuffer;

    Stocks(String path) throws FileNotFoundException {
        RandomAccessFile file = new RandomAccessFile(path, "r");
        stocksBuffer = new StocksBuffer(file);
    }

    public StockEntry get(int i) {
        byte[] stockEntryByteArray = new byte[0];

        try {
            stockEntryByteArray = stocksBuffer.getRecordNumbered(i);
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
        }

        return new StockEntry(ByteBuffer.wrap(stockEntryByteArray));
    }

    @Override
    public Iterator<StockEntry> iterator() {
        return new StockEntryIterator(stocksBuffer);
    }
}
