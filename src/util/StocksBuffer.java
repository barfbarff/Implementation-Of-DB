package util;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class StocksBuffer extends RecordsBuffer {

    private final int idByteSize = 8;
    private final int nameLengthByteSize = 2;
    private final int timestampByteSize = 8;
    private final int marketValueByteSize = 8;

    public StocksBuffer(RandomAccessFile file) {
        super(file);
    }

    @Override
    public int getSizeRecordAt(long recordStartIndex) {
        return idByteSize + getNameSizeRecordAt(recordStartIndex) + nameLengthByteSize
                + timestampByteSize + marketValueByteSize;
    }

    @Override
    public int getStartIndexRecordNumbered(long recordNumber) {
        int currentRecordStartIndex = 0;
        int currentRecordNameSize = getNameSizeRecordAt(currentRecordStartIndex);

        for (int i = 1; i <= recordNumber; i++) {
            currentRecordStartIndex += idByteSize + nameLengthByteSize
                    + currentRecordNameSize + timestampByteSize + marketValueByteSize;
            currentRecordNameSize = getNameSizeRecordAt(currentRecordStartIndex);
        }

        return currentRecordStartIndex;
    }

    private int getNameSizeRecordAt(long recordStartIndex) {
        byte[] byteArray = getByteArray(recordStartIndex + idByteSize, nameLengthByteSize);
        return ByteBuffer.wrap(byteArray).getShort();
    }
}
