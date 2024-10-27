package util.records;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StocksBuffer extends RecordsBuffer {

    private final int idByteSize = 8;
    private final int nameLengthByteSize = 2;
    private final int timestampByteSize = 8;
    private final int marketValueByteSize = 8;

    public StocksBuffer(RandomAccessFile file) {
        super(file);
    }

    @Override
    public void deleteRecordAt(long recordStartIndex) {}

    @Override
    public int getSizeRecordAt(long recordStartIndex) throws IOException {
        return idByteSize + getNameSizeRecordAt(recordStartIndex) + nameLengthByteSize
                + timestampByteSize + marketValueByteSize;
    }

    @Override
    public int getStartIndexRecordNumbered(long recordNumber) throws IOException {
        int currentRecordStartIndex = 0;
        int currentRecordNameSize = getNameSizeRecordAt(currentRecordStartIndex);
        int fixedByteSize = idByteSize + nameLengthByteSize + timestampByteSize + marketValueByteSize;

        for (int i = 1; i <= recordNumber; i++) {
            currentRecordStartIndex += fixedByteSize + currentRecordNameSize;
            currentRecordNameSize = getNameSizeRecordAt(currentRecordStartIndex);
        }

        return currentRecordStartIndex;
    }

    private int getNameSizeRecordAt(long recordStartIndex) throws IOException {
        return getByteBuffer(recordStartIndex + idByteSize, nameLengthByteSize).getShort();
    }
}
