package container.impl;

import container.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.MetaData;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MapContainerTest {
    private Container<Long, String> container;

    @BeforeEach
    public void setUp() {
        container = new MapContainer<>();
        container.open();
    }

    @Test
    public void testReserveUniqueKeys() {
        Long token1 = container.reserve();
        Long token2 = container.reserve();
        assertNotEquals(token1, token2, "Each key must be unique.");
        assertEquals(token1 + 1, token2, "Keys must follow a sequential order.");
    }

    @Test
    public void testBasicInsertAndRetrieve() {
        Long key = container.reserve();
        String targetValue = "testValue";
        container.update(key, targetValue);
        String retrievedValue = container.get(key);
        assertEquals(targetValue, retrievedValue, "Retrieved value should be same as inserted value.");
    }

    @Test
    public void testUpdateExistingEntry() {
        Long key = container.reserve();
        String initialValue = "initialValue";
        String updatedValue = "updatedValue";
        container.update(key, initialValue);
        container.update(key, updatedValue);
        String retrievedValue = container.get(key);
        assertEquals(updatedValue, retrievedValue, "Retrieved value should be same as updated value.");
    }

    @Test
    public void testRemoveEntry() {
        Long key = container.reserve();
        container.update(key, "testValue");
        container.remove(key);
        assertThrows(NoSuchElementException.class, () -> container.get(key), "Removed key should not be accessible.");
    }

    @Test
    public void testMetadataAccuracy() {
        MetaData metaData = container.getMetaData();
        assertNotNull(metaData, "MetaData cannot be empty/null.");
    }

    @Test
    public void testPersistentStorageForFileContainer() {
        if (container instanceof SimpleFileContainer) {
            Long key = container.reserve();
            String intendedValue = "persistentValue";
            container.update(key, intendedValue);
            container.close();
            container.open();
            String retrievedValue = container.get(key);
            assertEquals(intendedValue, retrievedValue, "The value should not change after the container is closed and reopened.");
        }
    }

    @Test
    public void testOpenAndCloseOperations() {
        container.close();
        assertThrows(IllegalStateException.class, () -> container.reserve(), "Operations should be unsuccessful when the container is closed.");
    }
}
