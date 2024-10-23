package container.impl;

import container.Container;
import util.MetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapContainer<Value> implements Container<Long, Value> {

	private final MetaData metaData = new MetaData();
	private final Map<Long, Value> hashMap = new HashMap<>();
	private boolean isOpen = false;
	
	public MapContainer() {
		metaData.setLongProperty("currentKey", 0L);
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
		isOpen = true;
	}

	@Override
	public void close() {
		isOpen = false;
	}
	
	@Override
	public Long reserve() throws IllegalStateException {
		if(!isOpen) {
			throw new IllegalStateException();
		}

		long newKey = metaData.getLongProperty("currentKey") + 1;
		metaData.setLongProperty("currentKey", newKey);
		return newKey;
	}
	

	@Override
	public Value get(Long key) throws NoSuchElementException {
		if(!isOpen) {
			throw new IllegalStateException();
		}

		return hashMap.get(key);
	}

	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		if(!isOpen) {
			throw new IllegalStateException();
		}

		hashMap.put(key, value);
	}

	@Override
	public void remove(Long key) throws NoSuchElementException {
		if(!isOpen) {
			throw new IllegalStateException();
		}

		hashMap.remove(key);
	}
}
