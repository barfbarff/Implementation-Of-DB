package util.records;

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

    protected ByteBuffer getByteBuffer(long startIndex, int length) throws IOException {
        byte[] byteArray = new byte[length];
        file.seek(startIndex);
        file.read(byteArray, 0, length);
        return ByteBuffer.wrap(byteArray);
    }

    public int getSizeRecordNumbered(long recordNumber) throws NoSuchElementException, IOException{
        return getSizeRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    public ByteBuffer getRecordAt(long recordStartIndex) throws IOException {
        return getByteBuffer(recordStartIndex, getSizeRecordAt(recordStartIndex));
    }

    public ByteBuffer getRecordNumbered(long recordNumber) throws NoSuchElementException, IOException{
        return getByteBuffer(getStartIndexRecordNumbered(recordNumber),
                             getSizeRecordNumbered(recordNumber));
    }

    public abstract void updateRecordAt(long recordStartIndex, ByteBuffer byteBuffer) throws IOException;

    public abstract void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) throws NoSuchElementException, IOException;

    public abstract void deleteRecordAt(long recordStartIndex) throws IOException;

    public abstract void deleteRecordNumbered(long recordNumber) throws NoSuchElementException, IOException;

    protected abstract int getSizeRecordAt(long recordStartIndex) throws NoSuchElementException, IOException;

    protected abstract int getStartIndexRecordNumbered(long recordNumber) throws NoSuchElementException, IOException;
}
