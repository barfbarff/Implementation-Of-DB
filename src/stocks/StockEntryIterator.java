package stocks;

import util.StocksBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StockEntryIterator implements Iterator<StockEntry> {

    private long pos;
    private final StocksBuffer stocksBuffer;

    public StockEntryIterator(StocksBuffer stocksBuffer) {
        this.stocksBuffer = stocksBuffer;
    }

    public boolean hasNext() {
        return pos < stocksBuffer.getSize();
    }

    public StockEntry next() {
        byte[] byteArray = new byte[0];

        try {
            byteArray = stocksBuffer.getRecordAt(pos);
            pos += stocksBuffer.getSizeRecordAt(pos);
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
        }

        return new StockEntry(ByteBuffer.wrap(byteArray));
    }
}
