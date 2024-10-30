package container.impl;

import container.Container;
import util.MetaData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


public class MapContainer<Value> implements Container<Long, Value> {

	private final MetaData metaData = new MetaData();

	private final Map<Long, Value> hashMap = new HashMap<>();
	private final Set<Long> deletedKeys = new HashSet<>();

	private final String currentKeyField = MetaDataFields.CURRENT_KEY
			.toString();

	private boolean isOpen = false;

	public MapContainer() {
		metaData.setLongProperty(currentKeyField, 0L);
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
		isOpen = true;
	}

	@Override
	public void close() {
		isOpen = false;
	}

	@Override
	public Long reserve() throws IllegalStateException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		long newKey = metaData.getLongProperty(currentKeyField) + 1;
		metaData.setLongProperty(currentKeyField, newKey);
		return newKey;
	}

	@Override
	public Value get(Long key) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		Value value = hashMap.get(key);
		if (value == null) {
			throw new NoSuchElementException();
		}

		return value;
	}

	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		Long currentKey = metaData.getLongProperty(currentKeyField);
		if (key > currentKey || deletedKeys.contains(key)) {
			throw new NoSuchElementException();
		}

		hashMap.put(key, value);
	}

	@Override
	public void remove(Long key) throws NoSuchElementException {
		if (!isOpen) {
			throw new IllegalStateException();
		}

		Value value = hashMap.remove(key);
		if (value == null) {
			throw new NoSuchElementException();
		} else {
			deletedKeys.add(key);
		}
	}
}
