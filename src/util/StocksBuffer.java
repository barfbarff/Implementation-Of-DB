package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class StocksBuffer {

    private final RandomAccessFile file;

    private final int idByteSize = 8;
    private final int nameLengthByteSize = 2;
    private final int timestampByteSize = 8;
    private final int marketValueByteSize = 8;

    private long size;

    public StocksBuffer(RandomAccessFile file) {
        this.file = file;
        try {
            size = file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getRecordAt(long recordStartIndex) {
        return getByteArray(recordStartIndex, getSizeRecordAt(recordStartIndex));
    }

    public byte[] getRecordNumbered(long recordNumber) {
        return getByteArray(getStartIndexRecordNumbered(recordNumber),
                            getSizeRecordNumbered(recordNumber));
    }

    public int getSizeRecordAt(long recordStartIndex) {
        return idByteSize + getNameSizeRecordAt(recordStartIndex) + nameLengthByteSize
                + timestampByteSize + marketValueByteSize;
    }

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

    public int getSizeRecordNumbered(long recordNumber) {
        return getSizeRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    public long getSize() {
        return size;
    }

    private int getNameSizeRecordAt(long recordStartIndex) {
        byte[] byteArray = getByteArray(recordStartIndex + idByteSize, nameLengthByteSize);
        return ByteBuffer.wrap(byteArray).getShort();
    }

    private byte[] getByteArray(long startIndex, int length) {
        byte[] byteArray = new byte[length];

        try{
            file.seek(startIndex);
            file.read(byteArray, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }
}
