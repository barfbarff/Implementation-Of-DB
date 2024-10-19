package stocks;

import util.StocksBuffer;

import java.nio.ByteBuffer;
import java.util.Iterator;

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
        byte[] byteArray = stocksBuffer.getRecordAt(pos);
        pos += stocksBuffer.getSizeRecordAt(pos);
        return new StockEntry(ByteBuffer.wrap(byteArray));
    }
}
