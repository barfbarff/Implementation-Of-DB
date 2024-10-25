package util.records;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

public class FixedSizeRecordsBuffer extends RecordsBuffer{

    private final int deletionFlagByteSize = 1;
    private final int fixedRecordSize;

    public FixedSizeRecordsBuffer(RandomAccessFile file, int keyByteSize, int valueByteSize) {
        super(file);
        this.fixedRecordSize = deletionFlagByteSize + keyByteSize + valueByteSize;
    }

    @Override
    public ByteBuffer getRecordNumbered(long recordNumber) throws NoSuchElementException, IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        int recordSize = getSizeRecordAt(startIndex);

        if (isRecordDeleted(startIndex)) {
            throw new NoSuchElementException();
        }

        return getByteBuffer(startIndex, recordSize);
    }

    @Override
    public void updateRecordAt(long recordStartIndex, ByteBuffer byteBuffer) throws IOException {
        file.seek(recordStartIndex);
        file.write(byteBuffer.array());
    }

    @Override
    public void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) throws NoSuchElementException, IOException {
        updateRecordAt(getStartIndexRecordNumbered(recordNumber), byteBuffer);
    }

    @Override
    public void deleteRecordAt(long recordStartIndex) throws IOException {
        file.seek(recordStartIndex);
        file.write((byte) 1);
    }


    @Override
    public void deleteRecordNumbered(long recordNumber) throws NoSuchElementException, IOException {
        deleteRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    @Override
    protected int getSizeRecordAt(long recordStartIndex) throws NoSuchElementException{
        if(recordStartIndex > getSize()) {
            throw new NoSuchElementException();
        }

        return fixedRecordSize;
    }

    @Override
    protected int getStartIndexRecordNumbered(long recordNumber) throws NoSuchElementException{
        int currentRecordStartIndex = 0;

        for (int i = 1; i <= recordNumber; i++) {
            currentRecordStartIndex += fixedRecordSize;
        }

        if (currentRecordStartIndex > getSize()) {
            throw new NoSuchElementException();
        }

        return currentRecordStartIndex;
    }

    private boolean isRecordDeleted(int startIndex) throws IOException {
        byte deletionFlag = getByteBuffer(startIndex, deletionFlagByteSize).get();
        return deletionFlag == 1;
    }
}
