package util;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class RecordsBuffer {

    private final RandomAccessFile file;
    private long size;

    public RecordsBuffer(RandomAccessFile file) {
        this.file = file;
        try {
            size = file.length();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public long getSize() {
        return size;
    }

    protected byte[] getByteArray(long startIndex, int length) {
        byte[] byteArray = new byte[length];

        try{
            file.seek(startIndex);
            file.read(byteArray, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }

    public byte[] getRecordNumbered(long recordNumber) {
        return getByteArray(getStartIndexRecordNumbered(recordNumber),
                getSizeRecordNumbered(recordNumber));
    }

    public byte[] getRecordAt(long recordStartIndex) {
        return getByteArray(recordStartIndex, getSizeRecordAt(recordStartIndex));
    }

    public int getSizeRecordNumbered(long recordNumber) {
        return getSizeRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    public abstract int getSizeRecordAt(long recordNumber);

    public abstract int getStartIndexRecordNumbered(long recordNumber);
}
