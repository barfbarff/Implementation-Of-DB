package container.test;

import container.Container;
import container.impl.MapContainer;
import container.impl.MetaDataFields;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.MetaData;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MapContainerTest {
    // TODO: Replace testing using Reflection by using mocks with Mockito

    static Container<Long, Integer> mapContainer = new MapContainer<>();
    static Map<Long, Integer> hashMap;
    static Set<Long> deletedKeys;
    static String currentKeyField = MetaDataFields.CURRENT_KEY
            .toString();

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void setUp() {
        try {
            Field hashMapField = mapContainer
                    .getClass()
                    .getDeclaredField("hashMap");
            Field deletedKeysField = mapContainer
                    .getClass()
                    .getDeclaredField("deletedKeys");

            hashMapField.setAccessible(true);
            deletedKeysField.setAccessible(true);

            hashMap = (Map<Long, Integer>) hashMapField
                    .get(mapContainer);
            deletedKeys = (Set<Long>) deletedKeysField
                    .get(mapContainer);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUpEachTestCase() {
        hashMap.clear();
        deletedKeys.clear();
        hashMap.put(0L, 123);
        hashMap.put(1L, 456);
        hashMap.put(2L, 789);
        hashMap.put(3L, 321);
        hashMap.put(4L, 654);
        hashMap.put(5L, 987);

        mapContainer.open();
        MetaData metaData = mapContainer.getMetaData();
        metaData.setLongProperty(currentKeyField, 5L);
        mapContainer.close();
    }


    @Test
    void testReserveClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            mapContainer.reserve();
        });
    }

    @Test
    void testReserveSuccessful() {
        mapContainer.open();

        long newKey = mapContainer.reserve();
        assertEquals(newKey, 6L);
        MetaData metaData = mapContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 6L);

        mapContainer.close();
    }

    @Test
    void testGetMetaDataClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            mapContainer.getMetaData();
        });
    }

    @Test
    void testGetMetaDataOpenContainer() {
        mapContainer.open();

        MetaData metaData = mapContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);

        mapContainer.close();
    }

    @Test
    void testGetClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            mapContainer.get(0L);
        });
    }

    @Test
    void testGetOutOfBound() {
        mapContainer.open();

        assertThrows(NoSuchElementException.class, () -> {
            mapContainer.get(6L);
        });

        mapContainer.close();
    }

    @Test
    void testGetSuccessful() {
        mapContainer.open();

        assertEquals(mapContainer.get(0L), 123);
        assertEquals(mapContainer.get(1L), 456);
        assertEquals(mapContainer.get(2L), 789);
        assertEquals(mapContainer.get(3L), 321);
        assertEquals(mapContainer.get(4L), 654);
        assertEquals(mapContainer.get(5L), 987);

        MetaData metaData = mapContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);

        mapContainer.close();
    }

    @Test
    void testGetDeletedRecord() {
        mapContainer.open();

        hashMap.remove(3L);
        deletedKeys.add(3L);
        assertThrows(NoSuchElementException.class, () -> {
            mapContainer.get(3L);
        });

        mapContainer.close();
    }

    @Test
    void testInsertClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            mapContainer.insert(1024);
        });
    }

    @Test
    void testInsertSuccessful() {
        mapContainer.open();

        mapContainer.insert(1024);
        mapContainer.insert(2048);
        mapContainer.insert(4096);
        assertEquals(hashMap.get(0L), 123);
        assertEquals(hashMap.get(1L), 456);
        assertEquals(hashMap.get(2L), 789);
        assertEquals(hashMap.get(3L), 321);
        assertEquals(hashMap.get(4L), 654);
        assertEquals(hashMap.get(5L), 987);
        assertEquals(hashMap.get(6L), 1024);
        assertEquals(hashMap.get(7L), 2048);
        assertEquals(hashMap.get(8L), 4096);

        MetaData metaData = mapContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 8L);

        mapContainer.close();
    }

    @Test
    void testUpdateClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            mapContainer.update(0L, 1024);
        });
    }

    @Test
    void testUpdateOutOfBound() {
        mapContainer.open();

        assertThrows(NoSuchElementException.class, () -> {
            mapContainer.update(9L, 1024);
        });

        mapContainer.close();
    }

    @Test
    void testUpdateSuccessful() {
        mapContainer.open();

        mapContainer.update(0L, 321);
        mapContainer.update(1L, 654);
        mapContainer.update(2L, 987);

        assertEquals(hashMap.get(0L), 321);
        assertEquals(hashMap.get(1L), 654);
        assertEquals(hashMap.get(2L), 987);
        assertEquals(hashMap.get(3L), 321);
        assertEquals(hashMap.get(4L), 654);
        assertEquals(hashMap.get(5L), 987);

        MetaData metaData = mapContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);

        mapContainer.close();
    }

    @Test
    void testUpdateRemovedRecord() {
        mapContainer.open();

        hashMap.remove(3L);
        deletedKeys.add(3L);
        assertThrows(NoSuchElementException.class, () -> {
           mapContainer.update(3L, 1024);
        });

        mapContainer.close();
    }

    @Test
    void testRemoveClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            mapContainer.remove(0L);
        });
    }

    @Test
    void testRemoveOutOfBound() {
        mapContainer.open();

        assertThrows(NoSuchElementException.class, () -> {
            mapContainer.remove(6L);
        });

        mapContainer.close();
    }

    @Test
    void testRemoveSuccessful() {
        mapContainer.open();

        mapContainer.remove(2L);
        deletedKeys.add(2L);
        assertEquals(hashMap.get(0L), 123);
        assertEquals(hashMap.get(1L), 456);
        assertNull(hashMap.get(2L));
        assertEquals(hashMap.get(3L), 321);
        assertEquals(hashMap.get(4L), 654);
        assertEquals(hashMap.get(5L), 987);

        MetaData metaData = mapContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);

        mapContainer.close();
    }

    @Test
    void testRemoveDeletedRecord() {
        mapContainer.open();

        hashMap.remove(3L);
        deletedKeys.add(3L);
        assertThrows(NoSuchElementException.class, () -> {
            mapContainer.remove(3L);
        });

        mapContainer.close();
    }
}
