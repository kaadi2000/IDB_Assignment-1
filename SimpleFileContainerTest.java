package container.impl;

import io.FixedSizeSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.MetaData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleFileContainerTest {
    private SimpleFileContainer<String> container;
    private final Path testDirectory = Paths.get("test_container");
    private final String filenamePrefix = "test";

    private FixedSizeSerializer<String> serializer = new FixedSizeSerializer<>() {
        @Override
        public void serialize(String value, ByteBuffer buffer) {
            byte[] data = value.getBytes();
            buffer.putInt(data.length);
            buffer.put(data);
        }


        @Override
        public String deserialize(ByteBuffer buffer) {
            int length = buffer.getInt();
            byte[] data = new byte[length];
            buffer.get(data);
            return new String(data);
        }

        public int getSerializedSize() {
            return Integer.BYTES + 255;
        }
    };

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(testDirectory);
        container = new SimpleFileContainer<>(testDirectory, filenamePrefix, serializer);
        container.open();
    }

    @AfterEach
    void tearDown() {
        container.close();
        try {
            Files.deleteIfExists(testDirectory.resolve(filenamePrefix + "_data.bin"));
            Files.deleteIfExists(testDirectory.resolve(filenamePrefix + "_meta.bin"));
            Files.deleteIfExists(testDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReserve() {
        Long key1 = container.reserve();
        Long key2 = container.reserve();
        assertEquals(0, key1);
        assertEquals(1, key2);
    }

    @Test
    void testUpdateAndGet() {
        Long key = container.reserve();
        String value = "Hello, World!";
        container.update(key, value);
        String retrievedValue = container.get(key);
        assertEquals(value, retrievedValue);
    }

    @Test
    void testGetNonExistentKey() {
        Long key = container.reserve();
        container.update(key, "Test");
        assertThrows(NoSuchElementException.class, () -> container.get(key + 1));
    }

    @Test
    void testRemove() {
        Long key = container.reserve();
        container.update(key, "To Be Removed");
        container.remove(key);
        assertThrows(NoSuchElementException.class, () -> container.get(key));
    }

    @Test
    void testMetaData() {
        MetaData metaData = container.getMetaData();
        assertNotNull(metaData);
    }

    @Test
    void testCloseAndOpen() {
        Long key = container.reserve();
        container.update(key, "Persistent Data");
        container.close();

        SimpleFileContainer<String> newContainer = new SimpleFileContainer<>(testDirectory, filenamePrefix, serializer);
        newContainer.open();
        String retrievedValue = newContainer.get(key);
        assertEquals("Persistent Data", retrievedValue);
        newContainer.close();
    }
}
