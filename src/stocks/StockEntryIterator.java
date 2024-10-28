package stocks;

import util.records.RecordsBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class StockEntryIterator implements Iterator<StockEntry> {

    private long pos;
    private final RecordsBuffer stocksBuffer;

    public StockEntryIterator(RecordsBuffer stocksBuffer) {
        this.stocksBuffer = stocksBuffer;
    }

    public boolean hasNext() {
        return pos < stocksBuffer.getSize();
    }

    public StockEntry next() {
        try {
            ByteBuffer byteBuffer = stocksBuffer.getRecordAt(pos);
            pos += stocksBuffer.getSizeRecordAt(pos);
            return new StockEntry(byteBuffer);
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
