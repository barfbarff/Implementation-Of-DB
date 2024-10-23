package util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

public class FixedSizeRecordsBuffer extends RecordsBuffer{

    private final int deletionFlagByteSize = 1;
    private final int keyByteSize;
    private final int valueByteSize;

    public FixedSizeRecordsBuffer(RandomAccessFile file, int keyByteSize, int valueByteSize) {
        super(file);
        this.keyByteSize = keyByteSize;
        this.valueByteSize = valueByteSize;
    }

    @Override
    public byte[] getRecordNumbered(long recordNumber) throws NoSuchElementException, IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        int trueRecordSize = getSizeRecordAt(startIndex) - deletionFlagByteSize;

        if (isRecordDeleted(startIndex)) {
            throw new NoSuchElementException();
        }

        return getByteArray(startIndex + deletionFlagByteSize, trueRecordSize);
    }

    @Override
    public void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) throws NoSuchElementException, IOException {
        file.seek(getStartIndexRecordNumbered(recordNumber) + deletionFlagByteSize);
        file.write(byteBuffer.array());
    }

    @Override
    public void deleteRecordNumbered(long recordNumber) throws NoSuchElementException, IOException {
        byte[] byteArray = getRecordNumbered(recordNumber);

        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        byteBuffer.put((byte) 1);
        byteBuffer.flip();

        file.seek(getStartIndexRecordNumbered(recordNumber));
        file.write(byteBuffer.array());
    }

    @Override
    protected int getSizeRecordAt(long recordStartIndex) throws NoSuchElementException{
        if(recordStartIndex > getSize()) {
            throw new NoSuchElementException();
        }

        return deletionFlagByteSize + keyByteSize + valueByteSize;
    }

    @Override
    protected int getStartIndexRecordNumbered(long recordNumber) throws NoSuchElementException{
        int currentRecordStartIndex = 0;

        for (int i = 1; i <= recordNumber; i++) {
            currentRecordStartIndex += deletionFlagByteSize + keyByteSize
                    + valueByteSize;
        }

        if (currentRecordStartIndex > getSize()) {
            throw new NoSuchElementException();
        }

        return currentRecordStartIndex;
    }

    private boolean isRecordDeleted(int startIndex) throws IOException {
        byte deletionFlag = getByteArray(startIndex, deletionFlagByteSize)[0];
        return deletionFlag == 1;
    }
}
