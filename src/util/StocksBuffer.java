package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

public class StocksBuffer extends RecordsBuffer {

    private final int idByteSize = 8;
    private final int nameLengthByteSize = 2;
    private final int timestampByteSize = 8;
    private final int marketValueByteSize = 8;

    public StocksBuffer(RandomAccessFile file) {
        super(file);
    }

    @Override
    public void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) {}

    @Override
    public void deleteRecordNumbered(long recordNumber) {}

    @Override
    public int getSizeRecordAt(long recordStartIndex) throws NoSuchElementException, IOException {
        if (recordStartIndex > getSize()) {
            throw new NoSuchElementException();
        }

        return idByteSize + getNameSizeRecordAt(recordStartIndex) + nameLengthByteSize
                + timestampByteSize + marketValueByteSize;
    }

    @Override
    protected int getStartIndexRecordNumbered(long recordNumber) throws NoSuchElementException, IOException {
        int currentRecordStartIndex = 0;
        int currentRecordNameSize = getNameSizeRecordAt(currentRecordStartIndex);

        for (int i = 1; i <= recordNumber; i++) {
            currentRecordStartIndex += idByteSize + nameLengthByteSize
                    + currentRecordNameSize + timestampByteSize + marketValueByteSize;
            currentRecordNameSize = getNameSizeRecordAt(currentRecordStartIndex);
        }

        if (currentRecordStartIndex > getSize()) {
            throw new NoSuchElementException();
        }

        return currentRecordStartIndex;
    }

    private int getNameSizeRecordAt(long recordStartIndex) throws IOException {
        byte[] byteArray = getByteArray(recordStartIndex + idByteSize, nameLengthByteSize);
        return ByteBuffer.wrap(byteArray).getShort();
    }

}
