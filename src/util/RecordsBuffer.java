package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

public abstract class RecordsBuffer {

    protected final RandomAccessFile file;
    private long size;

    public RecordsBuffer(RandomAccessFile file) {
        this.file = file;
        try {
            size = file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException{
        file.close();
    }

    public long getSize() {
        return size;
    }

    protected byte[] getByteArray(long startIndex, int length) throws IOException {
        byte[] byteArray = new byte[length];
        file.seek(startIndex);
        file.read(byteArray, 0, length);
        return byteArray;
    }

    public byte[] getRecordNumbered(long recordNumber) throws NoSuchElementException, IOException{
        return getByteArray(getStartIndexRecordNumbered(recordNumber),
                            getSizeRecordNumbered(recordNumber));
    }

    public byte[] getRecordAt(long recordStartIndex) throws NoSuchElementException, IOException {
        if (recordStartIndex > size) {
            throw new NoSuchElementException();
        }

        return getByteArray(recordStartIndex, getSizeRecordAt(recordStartIndex));
    }

    public int getSizeRecordNumbered(long recordNumber) throws NoSuchElementException, IOException{
        return getSizeRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    public abstract void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) throws NoSuchElementException, IOException;

    public abstract void deleteRecordNumbered(long recordNumber) throws NoSuchElementException, IOException;

    protected abstract int getSizeRecordAt(long recordStartIndex) throws NoSuchElementException, IOException;

    protected abstract int getStartIndexRecordNumbered(long recordNumber) throws NoSuchElementException, IOException;
}
