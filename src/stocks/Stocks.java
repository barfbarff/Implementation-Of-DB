package stocks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class Stocks implements Iterable<StockEntry> {

    private final RandomAccessFile file;

    private static final int idByteSize = 8;
    private static final int nameLengthByteSize = 2;
    private static final int timestampByteSize = 8;
    private static final int marketValueByteSize = 8;

    private record RecordMetada(int nameByteSize, int nameLengthOffset) {

        public int getOffset() {
            return nameLengthOffset - idByteSize;
        }

        public int getRecordSize() {
            return idByteSize + nameLengthByteSize + nameByteSize
                    + timestampByteSize + marketValueByteSize;
        }
    }

    Stocks(String path) throws FileNotFoundException {
        file = new RandomAccessFile(path, "r");
    }

    public StockEntry get(int i) {
        RecordMetada recordMetada = getRecordMetadata(i);

        byte[] stockEntryByteArray = getByteArray(recordMetada.getOffset(), recordMetada.getRecordSize());
        return new StockEntry(ByteBuffer.wrap(stockEntryByteArray));
    }

    private RecordMetada getRecordMetadata(int recordNumber) {
        int nameLengthOffset = idByteSize;
        int nameByteSize = getNameByteSize(nameLengthOffset);

        for (int i = 1; i<= recordNumber; i++) {
            nameLengthOffset += nameLengthByteSize + nameByteSize + timestampByteSize
                    + marketValueByteSize + idByteSize;
            nameByteSize = getNameByteSize(nameLengthOffset);
        }

        return new RecordMetada(nameByteSize, nameLengthOffset);
    }

    private int getNameByteSize(int nameLengthOffset) {
        byte[] byteArray = getByteArray(nameLengthOffset, nameLengthByteSize);
        return ByteBuffer.wrap(byteArray).getShort();
    }

    private byte[] getByteArray(int offset, int length) {
        byte[] byteArray = new byte[length];

        try{
            file.seek(offset);
            file.read(byteArray, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    @Override
    public Iterator<StockEntry> iterator() {
        return new StockEntryIterator(file);
    }
}
