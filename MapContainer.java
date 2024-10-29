package container.impl;

import container.Container;
import util.MetaData;

import java.util.NoSuchElementException;

import java.util.HashMap;
import java.util.Map;

public class MapContainer<Value> implements Container<Long, Value> {
	final Map<Long, Value> map;
	long key;
	boolean isOpen;
	public MapContainer() {
		// TODO
		this.map = new HashMap<>();
		this.key = 0;
		this.isOpen = false;

	}
	
	@Override
	public MetaData getMetaData() {
		// TODO
		return new MetaData();
	}
	
	@Override
	public void open() {
		// TODO
		isOpen = true;

	}

	@Override
	public void close() {
		// TODO
		isOpen = false;
	}
	
	@Override
	public Long reserve() throws IllegalStateException {
		// TODO
		if (!isOpen) {
			throw new IllegalStateException("Full");
		}
		return key++;
	}
	

	@Override
	public Value get(Long key) throws NoSuchElementException {
		// TODO
		if (!map.containsKey(key)) {
			throw new NoSuchElementException("No value found for key: " + key);
		}
		return map.get(key);
	}

	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		// TODO
		if (!map.containsKey(key)) {
			throw new NoSuchElementException("No value found for key: " + key);
		}
		map.put(key, value);
	}

	@Override
	public void remove(Long key) throws NoSuchElementException {
		// TODO
		if (!map.containsKey(key)) {
			throw new NoSuchElementException("No value found for key: " + key);
		}
		map.remove(key);
	}
}
