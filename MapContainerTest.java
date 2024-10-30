package container.impl;

import container.impl.MapContainer;
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
        Long key1 = container.reserve();
        Long key2 = container.reserve();
        assertNotEquals(key1, key2, "Keys should be unique");
        assertEquals(key1 + 1, key2, "Keys should be sequential");
    }

    @Test
    public void testBasicInsertAndRetrieve() {
        Long key = container.reserve();
        String expectedValue = "testValue";
        container.update(key, expectedValue);
        String retrievedValue = container.get(key);
        assertEquals(expectedValue, retrievedValue, "Retrieved value should match the inserted value");
    }

    @Test
    public void testUpdateExistingEntry() {
        Long key = container.reserve();
        String initialValue = "initialValue";
        String updatedValue = "updatedValue";
        container.update(key, initialValue);
        container.update(key, updatedValue);
        String retrievedValue = container.get(key);
        assertEquals(updatedValue, retrievedValue, "Retrieved value should match the updated value");
    }

    @Test
    public void testRemoveEntry() {
        Long key = container.reserve();
        container.update(key, "testValue");
        container.remove(key);
        assertThrows(NoSuchElementException.class, () -> container.get(key), "Removed key should not be accessible");
    }

    @Test
    public void testMetadataAccuracy() {
        MetaData metaData = container.getMetaData();
        assertNotNull(metaData, "MetaData should not be null");
    }

    @Test
    public void testPersistentStorageForFileContainer() {
        if (container instanceof SimpleFileContainer) {
            Long key = container.reserve();
            String expectedValue = "persistentValue";
            container.update(key, expectedValue);
            container.close(); // Close and reopen to simulate persistence
            container.open();
            String retrievedValue = container.get(key);
            assertEquals(expectedValue, retrievedValue, "Value should persist after closing and reopening container");
        }
    }

    @Test
    public void testOpenAndCloseOperations() {
        container.close();
        assertThrows(IllegalStateException.class, () -> container.reserve(), "Operations should fail when container is closed");
    }
}
