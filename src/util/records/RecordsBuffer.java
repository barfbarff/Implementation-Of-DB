package util.records;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Acts as a buffer for reading or updating records in the forms of bytes in a file.
 * Specifically, this class provides functions for convenient reading and writing
 * of bytes that correspond to records in the file.
 */
public abstract class RecordsBuffer {

    protected final RandomAccessFile file;

    public RecordsBuffer(RandomAccessFile file) {
        this.file = file;
    }

    /**
     * Clears the content of the file associated with this buffer
     * @throws IOException if an I/O error occurs while setting the length of this file
     */
    public void clear() throws IOException {
        file.setLength(0);
    }

    /**
     * Closes the file associated with this buffer, so that system resources that are linked
     * to this file can be released
     * @throws IOException if an I/O error occurs while closing the file
     */
    public void close() throws IOException {
        file.close();
    }

    /**
     * Gets the length of the file associated with this buffer
     * @return Length of the file
     */
    public long getSize() {
        try {
            return file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Retrieves ByteBuffer containing a chunk of bytes from the file associated with this buffer.
     * The chunk begins at the specified starting index, and has the specified length.
     * @param startIndex the position in the file where the chunk begins.
     * @param length the number of bytes to include in the chunk.
     * @return the ByteBuffer containing the specified chunk of bytes from the file.
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public ByteBuffer getByteBuffer(long startIndex, int length) throws IOException {
        byte[] byteArray = new byte[length];
        file.seek(startIndex);
        file.read(byteArray, 0, length);
        return ByteBuffer.wrap(byteArray);
    }

    /**
     * Retrieves the size of the ByteBuffer from the file associated with this buffer
     * corresponding to the record identified by the given record number.
     * @param recordNumber the numeric position of the record in the file.
     * @return the size of the ByteBuffer corresponding to the record.
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public int getSizeRecordNumbered(long recordNumber) throws IOException {
        return getSizeRecordAt(getStartIndexRecordNumbered(recordNumber));
    }

    /**
     * Retrieves the ByteBuffer from the file associated with this buffer corresponding
     * to the record starting at the specified starting index.
     * @param recordStartIndex the starting index of the record in the file.
     * @return the ByteBuffer corresponding to the record.
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public ByteBuffer getRecordAt(long recordStartIndex) throws IOException {
        return getByteBuffer(recordStartIndex, getSizeRecordAt(recordStartIndex));
    }

    /**
     * Retrieves the ByteBuffer from the file associated with this buffer corresponding
     * to the record identified by the given record number.
     * @param recordNumber the numeric position of the record in the file.
     * @return the ByteBuffer corresponding to the record.
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public ByteBuffer getRecordNumbered(long recordNumber) throws IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        int recordSize = getSizeRecordAt(startIndex);
        return getByteBuffer(startIndex, recordSize);
    }

    /**
     * Append the specified ByteBuffer to the file associated with this buffer.
     * @param byteBuffer the ByteBuffer containing bytes to be appended to the file.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void append(ByteBuffer byteBuffer) throws IOException {
        file.seek(getSize());
        file.write(byteBuffer.array());
    }

    /**
     * Updates the record at the specified starting index in the file associated with
     * this buffer by writing the bytes in the given ByteBuffer to the file starting
     * from the specified starting index.
     * @param recordStartIndex the starting index of the record in the file.
     * @param byteBuffer the ByteBuffer containing the bytes of the record to be updated.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void updateRecordAt(long recordStartIndex, ByteBuffer byteBuffer) throws IOException {
        file.seek(recordStartIndex);
        file.write(byteBuffer.array());
    }

    /**
     * Updates the chunk of bytes in the file associated with this buffer corresponding
     * to the record identified by the given record number with the bytes in the given
     * ByteBuffer
     * @param recordNumber the numeric position of the record in the file.
     * @param byteBuffer the ByteBuffer containing the bytes of the record to be updated.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void updateRecordNumbered(long recordNumber, ByteBuffer byteBuffer) throws IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        updateRecordAt(startIndex, byteBuffer);
    }

    /**
     * Deletes the record at the specified starting index the file associated with this
     * buffer. Note that deletion does not necessarily mean that the chunk of bytes
     * corresponding to the record is physically removed from the file. It may simply
     * mark the record as deleted.
     * @param recordStartIndex the starting index of the record in the file.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public abstract void deleteRecordAt(long recordStartIndex) throws IOException;

    /**
     * Deletes the record identified by the given record number in the file associated
     * with this buffer. Note that deletion does not necessarily mean that the chunk of
     * bytes corresponding to the record is physically removed from the file. It may
     * simply mark the record as deleted.
     * @param recordNumber the numeric position of the record in the file.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void deleteRecordNumbered(long recordNumber) throws IOException {
        int startIndex = getStartIndexRecordNumbered(recordNumber);
        deleteRecordAt(startIndex);
    }

    /**
     * Retrieves the size of the ByteBuffer from the file associated with this buffer,
     * corresponding to the record at the specified starting index. For records of
     * fixed size, their sizes are identical to each other. However, for records
     * of dynamic size, the sizes can only be resolved during runtime.
     * @param recordStartIndex the starting index of the record in the file.
     * @return the size of the ByteBuffer corresponding to the record.
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public abstract int getSizeRecordAt(long recordStartIndex) throws IOException;

    /**
     * Retrieves the starting position of the record identified by the given record
     * number in the file associated with this buffer.
     * @param recordNumber the numeric position of the record in the file.
     * @return the starting position of the record in the file.
     * @throws IOException if an I/O error occurs while reading from the file.
     */
    public abstract int getStartIndexRecordNumbered(long recordNumber) throws IOException;
}
