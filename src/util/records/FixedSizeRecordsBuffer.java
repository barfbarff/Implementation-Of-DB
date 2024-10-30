package util.records;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FixedSizeRecordsBuffer extends RecordsBuffer {

    private final int deletionFlagByteSize = 1;
    private final int keyByteSize;
    private final int valueByteSize;
    private final int fixedRecordSize;

    public FixedSizeRecordsBuffer(RandomAccessFile file, int keyByteSize, int valueByteSize) {
        super(file);
        this.keyByteSize = keyByteSize;
        this.valueByteSize = valueByteSize;
        this.fixedRecordSize = deletionFlagByteSize + keyByteSize + valueByteSize;
    }

    @Override
    public void deleteRecordAt(long recordStartIndex) throws IOException {
        file.seek(recordStartIndex);
        file.write((byte) 1);
    }

    @Override
    public int getSizeRecordAt(long recordStartIndex) {
        return fixedRecordSize;
    }

    @Override
    public int getStartIndexRecordNumbered(long recordNumber) {
        int currentRecordStartIndex = 0;

        for (int i = 1; i <= recordNumber; i++) {
            currentRecordStartIndex += fixedRecordSize;
        }

        return currentRecordStartIndex;
    }
}
