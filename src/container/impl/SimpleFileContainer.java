package container.impl;

import container.Container;
import io.FixedSizeSerializer;
import io.LongSerializer;
import util.ContainerRuntimeException;
import util.FixedSizeRecordsBuffer;
import util.MetaData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class SimpleFileContainer<Value> implements Container<Long, Value> {

	private FixedSizeRecordsBuffer recordsBuffer;

	private boolean isOpen = false;

	private final String pathToDataFile;
	private final String pathToMetadataFile;

	private final LongSerializer keySerializer;
	private final FixedSizeSerializer<Value> valueSerializer;

	private final MetaData metaData;

	public SimpleFileContainer(Path directory, String filenamePrefix, FixedSizeSerializer<Value> serializer) {
		this.pathToDataFile = directory.resolve(filenamePrefix + "_data.bin").toString();
		this.pathToMetadataFile = directory.resolve(filenamePrefix + "_meta.bin").toString();

		this.keySerializer = new LongSerializer();
		this.valueSerializer = serializer;

		this.metaData = new MetaData();
		try {
			metaData.readFrom(Path.of(pathToMetadataFile));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public MetaData getMetaData() {
		if(!isOpen) {
			throw new IllegalStateException();
		}

		return metaData;
	}

	@Override
	public void open() {
		try {
			RandomAccessFile file = new RandomAccessFile(pathToDataFile, "rw");
			recordsBuffer = new FixedSizeRecordsBuffer(file, keySerializer.getSerializedSize(),
					valueSerializer.getSerializedSize());
			isOpen = true;
		} catch(FileNotFoundException e){
			throw new ContainerRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		try {
			recordsBuffer.close();
			isOpen = false;
		} catch(IOException e) {
			throw new ContainerRuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public Long reserve() throws IllegalStateException {
		if(!isOpen) {
			throw new IllegalStateException();
		}

		long newKey = metaData.getLongProperty("currentKey") + 1;
		metaData.setLongProperty("currentKey", newKey);

		try {
			metaData.writeTo(Path.of(pathToMetadataFile));
		} catch(IOException e) {
			e.printStackTrace();
		}

		return newKey;
	}
	
	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		if (key > metaData.getLongProperty("currentKey")) {
			throw new NoSuchElementException();
		}

		try {
			byte[] byteArray = recordsBuffer.getRecordNumbered(key);

			ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
			keySerializer.serialize(key, byteBuffer);
			valueSerializer.serialize(value, byteBuffer);

			byteBuffer.flip();

			recordsBuffer.updateRecordNumbered(key, byteBuffer);

			long newSize = metaData.getLongProperty("size") + byteBuffer.limit();
			metaData.setLongProperty("size", newSize);
			metaData.writeTo(Path.of(pathToMetadataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Value get(Long key) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		if (key > metaData.getLongProperty("currentkey")) {
			throw new NoSuchElementException();
		}

		Value value = null;
		try {
			byte[] byteArray = recordsBuffer.getRecordNumbered(key);
			
			ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
			keySerializer.deserialize(byteBuffer);
			value = valueSerializer.deserialize(byteBuffer);
		} catch (IOException e) {
            e.printStackTrace();
        }
		
		return value;
    }

	@Override
	public void remove(Long key) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		if (key > metaData.getLongProperty("currentkey")) {
			throw new NoSuchElementException();
		}

		try {
			recordsBuffer.deleteRecordNumbered(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
