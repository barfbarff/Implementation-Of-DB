package container.test.utils;

import container.impl.MetaDataFields;
import io.FixedSizeSerializer;
import io.LongSerializer;
import util.MetaData;
import util.records.FixedSizeRecordsBuffer;
import util.records.RecordsBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * Utility class for reading or updating records in the form of bytes in a test data file,
 * as well as initializing a test metadata file to facilitate testing of SimpleFileContainer.
 * @param <T> a Serializer used for serializing and deserializing values of fixed size.
 * @param <V> the type of values to be stored in the test file.
 */
public class SimpleFileContainerTestUtils<T extends FixedSizeSerializer<V>, V> {
    private final RecordsBuffer dataBuffer;

    private final Path pathToMetadataFile;

    private final LongSerializer keySerializer = new LongSerializer();
    private final T valueSerializer;

    public SimpleFileContainerTestUtils(Path directory, T valueSerializer) {
        String pathToDataFile = directory.resolve( "test_data.bin").toString();
        pathToMetadataFile = Path.of(directory.resolve("test_meta.bin").toString());
        RandomAccessFile dataFile = null;

        try {
             dataFile = new RandomAccessFile(pathToDataFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        dataBuffer = new FixedSizeRecordsBuffer(dataFile, keySerializer.getSerializedSize(),
                valueSerializer.getSerializedSize());

        this.valueSerializer = valueSerializer;
    }

    /**
     * Clears the content of the test data file associated with the data buffer.
     * This method is used to reset the file content before each test case, preventing
     * conflicts between different test scenarios.
     * @throws IOException if an I/O error occurs while clearing the file.
     */
    public void clearDataFile() throws IOException {
        dataBuffer.clear();
    }

    /**
     * Initializes the content of the test metadata file.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void initMetadataFile() throws IOException {
        MetaData metaData = new MetaData();
        metaData.setLongProperty(MetaDataFields.CURRENT_KEY.toString(), -1);
        metaData.setLongProperty(MetaDataFields.CURRENT_SIZE.toString(), 0);
        metaData.writeTo(pathToMetadataFile);
    }

    /**
     * Appends a chunk of bytes representing a record with the specified key and value
     * to the end of the test data file associated with the data buffer.
     * @param key the unique identifier for the record.
     * @param value the data to be stored in the record associated with the key.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void append(Long key, V value) throws IOException {
        int deletionFlagByteSize = 1;
        int byteBufferSize = keySerializer.getSerializedSize() + valueSerializer.getSerializedSize() +
                deletionFlagByteSize;
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferSize);

        byteBuffer.put((byte) 0);
        keySerializer.serialize(key, byteBuffer);
        valueSerializer.serialize(value, byteBuffer);
        byteBuffer.flip();

        MetaData metaData = new MetaData();
        metaData.readFrom(pathToMetadataFile);
        long newKey = metaData.getLongProperty(MetaDataFields.CURRENT_KEY.toString()) + 1;
        long newSize = metaData.getLongProperty(MetaDataFields.CURRENT_SIZE.toString()) + byteBufferSize;
        metaData.setLongProperty(MetaDataFields.CURRENT_KEY.toString(), newKey);
        metaData.setLongProperty(MetaDataFields.CURRENT_SIZE.toString(), newSize);

        metaData.writeTo(pathToMetadataFile);
        dataBuffer.append(byteBuffer);
    }

    /**
     * Retrieves the value associated with the specified key from the test data file.
     * @param key the unique identifier for the record.
     * @return the value associated with the specified key.
     * @throws IOException if an I/O error occurs while reading from the file.
     * @throws NoSuchElementException if the record with the specified key does not
     * exist in the file.
     */
    public V get(Long key) throws NoSuchElementException, IOException {
        ByteBuffer byteBuffer = dataBuffer.getRecordNumbered(key);

        byte deletionFlag = byteBuffer.get();
        if (deletionFlag == 1) {
            throw new NoSuchElementException();
        }

        keySerializer.deserialize(byteBuffer);
        return valueSerializer.deserialize(byteBuffer);
    }

    /**
     * Deletes the record with the specified key from the test data file.
     * @param key the unique identifier for the record.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    public void delete(Long key) throws IOException {
        ByteBuffer byteBuffer = dataBuffer.getRecordNumbered(key);
        byteBuffer.put((byte) 1);
        byteBuffer.flip();
        dataBuffer.updateRecordNumbered(key, byteBuffer);
    }

    /**
     * Closes the test data file associated with the data buffer, so that system resources
     * that are linked to this file can be released.
     */
    public void destroy() {
        try {
            dataBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
