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

public class SimpleFileContainerTestUtils<T extends FixedSizeSerializer<V>, V> {
    private final RecordsBuffer recordsBuffer;

    private final Path pathToMetadataFile;

    private final LongSerializer keySerializer = new LongSerializer();
    private final T valueSerializer;

    public SimpleFileContainerTestUtils(Path directory, T valueSerializer) {
        String pathToDataFile = directory.resolve( "test_data.bin").toString();
        pathToMetadataFile = Path.of(directory.resolve("test_meta.bin").toString());
        RandomAccessFile file = null;

        try {
             file = new RandomAccessFile(pathToDataFile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        recordsBuffer = new FixedSizeRecordsBuffer(file, keySerializer.getSerializedSize(),
                valueSerializer.getSerializedSize());

        this.valueSerializer = valueSerializer;
    }

    public void clearDataFile() throws IOException {
        recordsBuffer.clear();
    }

    public void initMetadataFile() throws IOException {
        MetaData metaData = new MetaData();
        metaData.setLongProperty(MetaDataFields.CURRENT_KEY.toString(), -1);
        metaData.setLongProperty(MetaDataFields.CURRENT_SIZE.toString(), 0);
        metaData.writeTo(pathToMetadataFile);
    }

    public void append(Long key, V value) throws IOException {
        int byteBufferSize = keySerializer.getSerializedSize() + valueSerializer.getSerializedSize() + 1;
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
        recordsBuffer.append(byteBuffer);
    }

    public V get(Long key) throws IOException{
        ByteBuffer byteBuffer = recordsBuffer.getRecordNumbered(key);
        byteBuffer.get();
        keySerializer.deserialize(byteBuffer);
        return valueSerializer.deserialize(byteBuffer);
    }

    public byte getDeletionFlag(Long key) throws IOException {
        ByteBuffer byteBuffer = recordsBuffer.getRecordNumbered(key);
        return byteBuffer.get();
    }

    public void destroy() {
        try {
            recordsBuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
