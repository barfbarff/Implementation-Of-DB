package container.impl;

import container.Container;
import util.MetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapContainer<Value> implements Container<Long, Value> {

	private final MetaData metaData = new MetaData();
	private final Map<Long, Value> hashMap = new HashMap<>();
	
	public MapContainer() {
		metaData.setIntProperty("isOpen", 0);
		metaData.setLongProperty("currentKey", 0L);
	}
	
	@Override
	public MetaData getMetaData() {
		return metaData;
	}
	
	@Override
	public void open() {
		metaData.setIntProperty("isOpen", 1);
	}

	@Override
	public void close() {
		metaData.setIntProperty("isOpen", 0);
	}
	
	@Override
	public Long reserve() throws IllegalStateException {
		if(metaData.getIntProperty("isOpen") == 0) {
			throw new IllegalStateException();
		}

		long newKey = metaData.getLongProperty("currentKey") + 1;
		metaData.setLongProperty("currentKey", newKey);
		return newKey;
	}
	

	@Override
	public Value get(Long key) throws NoSuchElementException {
		return hashMap.get(key);
	}

	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		hashMap.put(key, value);
	}

	@Override
	public void remove(Long key) throws NoSuchElementException {
		hashMap.remove(key);
	}
}
