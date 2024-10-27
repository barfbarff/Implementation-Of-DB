package util.records;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public abstract class RecordsBuffer {

    protected final RandomAccessFile file;

    public RecordsBuffer(RandomAccessFile file) {
        this.file = file;
    }

    public void clear() throws IOException{
        file.setLength(0);
    }

    public void close() throws IOException{
        file.close();
    }

    public long getSize() {
        try {
            return file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public ByteBuffer getByteBuffer(long startIndex, int length) throws IOException {
        byte[] byteArray = new byte[length];
        file.seek(startIndex);
        file.read(byteArray, 0, length);
        return ByteBuffer.wrap(byteArray);
    }

    public int getSizeRecordNumbered(long recordNumber) throws IOException{
        return getSizeRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    public ByteBuffer getRecordAt(long recordStartIndex) throws IOException {
        return getByteBuffer(recordStartIndex, getSizeRecordAt(recordStartIndex));
    }

    public ByteBuffer getRecordNumbered(long recordNumber) throws IOException{
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        int recordSize = getSizeRecordAt(startIndex);
        return getByteBuffer(startIndex, recordSize);
    }

    public void append(ByteBuffer byteBuffer) throws IOException {
        file.seek(getSize());
        file.write(byteBuffer.array());
    }

    public void updateRecordAt(long recordStartIndex, ByteBuffer byteBuffer) throws IOException {
        file.seek(recordStartIndex);
        file.write(byteBuffer.array());
    }

    public void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) throws IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        updateRecordAt(startIndex, byteBuffer);
    }

    public abstract void deleteRecordAt(long recordStartIndex) throws IOException;

    public void deleteRecordNumbered(long recordNumber) throws IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        deleteRecordAt(startIndex);
    }

    public abstract int getSizeRecordAt(long recordStartIndex) throws IOException;

    public abstract int getStartIndexRecordNumbered(long recordNumber) throws IOException;
}
