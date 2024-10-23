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

    public abstract byte[] getRecordAt(long recordStartIndex);

    public abstract byte[] getRecordNumbered(long recordNumber);

    public abstract int getSizeRecordAt(long recordNumber);

    public abstract int getSizeRecordNumbered(long recordNumber);

    public abstract int getStartIndexRecordNumbered(long recordNumber);
}
