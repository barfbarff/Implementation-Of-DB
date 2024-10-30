package container.impl;

import container.Container;
import io.FixedSizeSerializer;
import io.LongSerializer;
import util.ContainerRuntimeException;
import util.records.FixedSizeRecordsBuffer;
import util.MetaData;
import util.records.RecordsBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class SimpleFileContainer<Value> implements Container<Long, Value> {

	private RecordsBuffer recordsBuffer;

	private boolean isOpen = false;

	private final String pathToDataFile;
	private final String pathToMetadataFile;

	private final LongSerializer keySerializer;
	private final FixedSizeSerializer<Value> valueSerializer;

	private final String currentKeyField = MetaDataFields.CURRENT_KEY
			.toString();
	private final String currentSizeField = MetaDataFields.CURRENT_SIZE
			.toString();

	private final MetaData metaData;

	public SimpleFileContainer(Path directory, String filenamePrefix, FixedSizeSerializer<Value> serializer) {
		this.pathToDataFile = directory
				.resolve(filenamePrefix + FilesPostfix.DATA)
				.toString();
		this.pathToMetadataFile = directory
				.resolve(filenamePrefix + FilesPostfix.METADATA)
				.toString();

		this.keySerializer = new LongSerializer();
		this.valueSerializer = serializer;

		this.metaData = new MetaData();
		try {
			metaData.readFrom(Path.of(pathToMetadataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MetaData getMetaData() {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		return metaData;
	}

	@Override
	public void open() {
		try {
			RandomAccessFile file = new RandomAccessFile(
					pathToDataFile, "rw");

			int keySize = keySerializer.getSerializedSize();
			int valueSize = valueSerializer.getSerializedSize();
			recordsBuffer = new FixedSizeRecordsBuffer(file,
					keySize, valueSize);
			isOpen = true;
		} catch (FileNotFoundException e) {
			throw new ContainerRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		try {
			recordsBuffer.close();
			isOpen = false;
		} catch (IOException e) {
			throw new ContainerRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public Long reserve() throws IllegalStateException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		long newKey = metaData.getLongProperty(currentKeyField) + 1;
		metaData.setLongProperty(currentKeyField, newKey);

		try {
			metaData.writeTo(Path.of(pathToMetadataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return newKey;
	}

	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		if (key > metaData.getLongProperty(currentKeyField)) {
			throw new NoSuchElementException();
		}

		try {
			int startIndex = recordsBuffer
					.getStartIndexRecordNumbered(key);
			int recordSize = recordsBuffer
					.getSizeRecordAt(key);

			ByteBuffer byteBuffer = recordsBuffer
					.getByteBuffer(startIndex, recordSize);
			byte deletionFlag = byteBuffer.get();
			if (deletionFlag == 1) {
				throw new NoSuchElementException();
			}

			byteBuffer.position(0);
			byteBuffer.put((byte) 0);
			keySerializer.serialize(key, byteBuffer);
			valueSerializer.serialize(value, byteBuffer);

			byteBuffer.flip();

			recordsBuffer.updateRecordNumbered(key, byteBuffer);

			int endIndex = startIndex + recordSize;
			long currentFileSize = metaData
					.getLongProperty(currentSizeField);
			if (endIndex > currentFileSize) {
				long newSize = currentFileSize
						       + byteBuffer.limit()
						       + 1;
				metaData.setLongProperty(currentSizeField, newSize);
				metaData.writeTo(Path.of(pathToMetadataFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Value get(Long key) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		if (key > metaData.getLongProperty(currentKeyField)) {
			throw new NoSuchElementException();
		}

		Value value = null;
		try {
			ByteBuffer byteBuffer = recordsBuffer
					.getRecordNumbered(key);

			byte deletionFlag = byteBuffer.get();
			if (deletionFlag == 1) {
				throw new NoSuchElementException();
			}

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

		if (key > metaData.getLongProperty(currentKeyField)) {
			throw new NoSuchElementException();
		}

		try {
			int startIndex = recordsBuffer
					.getStartIndexRecordNumbered(key);

			ByteBuffer byteBuffer = recordsBuffer
					.getRecordAt(startIndex);
			byte deletionFlag = byteBuffer.get();
			if (deletionFlag == 1) {
				throw new NoSuchElementException();
			}

			recordsBuffer.deleteRecordAt(startIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
