package container.impl;

import container.Container;
import io.FixedSizeSerializer;
import util.MetaData;

import java.nio.file.Path;
import java.util.NoSuchElementException;

import java.io.RandomAccessFile;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SimpleFileContainer<Value> implements Container<Long, Value> {
	private final Path dataFilePath;
	private final Path metadataFilePath;
	private final FixedSizeSerializer<Value> serializer;
	private RandomAccessFile dataFile;
	private RandomAccessFile metadataFile;
	private boolean isOpen;
	private long key;
	private final int estimatedEntrySize;

	public SimpleFileContainer(Path directory, String filenamePrefix, FixedSizeSerializer<Value> serializer) {
		// TODO
		this.dataFilePath = directory.resolve(filenamePrefix + "_data.bin");
		this.metadataFilePath = directory.resolve(filenamePrefix + "_meta.bin");
		this.serializer = serializer;
		this.isOpen = false;
		this.key = 0;

		// Use reflection to create a sample instance for size estimation
		Value sampleValue;
		try {
			sampleValue = (Value) serializer.getClass().getComponentType().getDeclaredConstructor().newInstance();
			ByteBuffer buffer = ByteBuffer.allocate(1024); // Allocate a reasonable buffer size
			serializer.serialize(sampleValue, buffer);
			this.estimatedEntrySize = buffer.position();  // Store the estimated size
		} catch (Exception e) {
			throw new RuntimeException("Failed to estimate serialized object size", e);
		}

	}
	
	@Override
	public MetaData getMetaData() {
		// TODO
		MetaData metaData = new MetaData();
		try {
			if (metadataFile.length() > 0) {
				metadataFile.seek(0);  // Start of the metadata file
				int size = metadataFile.readInt();
				long nextAvailableKey = metadataFile.readLong();
				// Use these values internally as needed
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return metaData;
	}

	@Override
	public void open() {
		// TODO
		try {
			dataFile = new RandomAccessFile(dataFilePath.toFile(), "rw");
			metadataFile = new RandomAccessFile(metadataFilePath.toFile(), "rw");
			isOpen = true;
			if (metadataFile.length() > 0) {
				metadataFile.seek(0);
				int size = metadataFile.readInt();
				key = metadataFile.readLong();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Could not open files", e);
		}
		
	}

	@Override
	public void close() {
		// TODO
		try {
			if (metadataFile != null) {
				metadataFile.seek(0);
				metadataFile.writeInt((int) key); // Example of writing size
				metadataFile.writeLong(key);
				metadataFile.close();
			}
			if (dataFile != null) {
				dataFile.close();
			}
			isOpen = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Long reserve() throws IllegalStateException {
		// TODO
		if (!isOpen) {
			throw new IllegalStateException("Container is not open");
		}
		return key++;
	}
	
	@Override
	public void update(Long key, Value value) throws NoSuchElementException {
		// TODO
		try {
			long position = key * (estimatedEntrySize + 1 + Integer.BYTES);
			dataFile.seek(position);

			dataFile.writeByte(1);  // Mark as active entry

			ByteBuffer buffer = ByteBuffer.allocate(estimatedEntrySize);
			serializer.serialize(value, buffer);

			dataFile.writeInt(buffer.position());  // Length of data
			dataFile.write(buffer.array(), 0, buffer.position());  // Serialized data
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Value get(Long key) throws NoSuchElementException {
		// TODO
		try {
			long position = key * (estimatedEntrySize + 1 + Integer.BYTES);
			dataFile.seek(position);

			byte status = dataFile.readByte();
			if (status != 1) throw new NoSuchElementException("Key not found or marked as deleted");

			int dataLength = dataFile.readInt();
			byte[] data = new byte[dataLength];
			dataFile.read(data);

			ByteBuffer buffer = ByteBuffer.wrap(data);
			return serializer.deserialize(buffer);
		} catch (IOException e) {
			throw new NoSuchElementException("Error accessing data for the key");
		}
	}

	@Override
	public void remove(Long key) throws NoSuchElementException {
		// TODO
		try {
			long position = key * (estimatedEntrySize + 1 + Integer.BYTES);
			dataFile.seek(position);
			dataFile.writeByte(0);  // Mark as deleted entry
		} catch (IOException e) {
			throw new NoSuchElementException("Error removing data for the key");
		}
	}
}
