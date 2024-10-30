package container.test;

import container.Container;
import container.impl.MetaDataFields;
import container.impl.SimpleFileContainer;
import container.test.utils.SimpleFileContainerTestUtils;
import io.IntSerializer;
import org.junit.jupiter.api.*;
import util.MetaData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleFileContainerTest {
    // TODO: Replace testing against real files by using mocks with Mockito
    // TODO: Implement integration tests to test the functions against real files

    static Container<Long, Integer> simpleFileContainer;
    static SimpleFileContainerTestUtils<IntSerializer, Integer> utils;
    static String fileNamePrefix = "test";
    static String currentKeyField = MetaDataFields.CURRENT_KEY
            .toString();
    static String currentSizeField = MetaDataFields.CURRENT_SIZE
            .toString();

    @BeforeEach
    void setUpEachTestCase() {
        Path path = Path
                .of("src/container/data")
                .toAbsolutePath();
        IntSerializer intSerializer = new IntSerializer();
        utils = new SimpleFileContainerTestUtils<>(path,
                intSerializer);

        try {
            utils.clearDataFile();
            utils.initMetadataFile();
            utils.append(0L, 123);
            utils.append(1L, 456);
            utils.append(2L, 789);
            utils.append(3L, 321);
            utils.append(4L, 654);
            utils.append(5L, 987);
        } catch (IOException e) {
            e.printStackTrace();
        }

        simpleFileContainer = new SimpleFileContainer<>(path,
                fileNamePrefix, intSerializer);
    }

    @AfterAll
    static void cleanUp() {
        utils.destroy();
    }

    @Test
    void testReserveClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            simpleFileContainer.reserve();
        });
    }

    @Test
    void testReserveSuccessful() {
        simpleFileContainer.open();

        long newKey = simpleFileContainer.reserve();
        assertEquals(newKey, 6L);
        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 6L);

        simpleFileContainer.close();
    }

    @Test
    void testGetMetaDataClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            simpleFileContainer.getMetaData();
        });
    }

    @Test
    void testGetMetaDataOpenContainer() {
        simpleFileContainer.open();

        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);
        assertEquals(metaData.getLongProperty(currentSizeField),78);

        simpleFileContainer.close();
    }

    @Test
    void testGetClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            simpleFileContainer.get(0L);
        });
    }

    @Test
    void testGetOutOfBound() {
        simpleFileContainer.open();

        assertThrows(NoSuchElementException.class, () -> {
            simpleFileContainer.get(6L);
        });

        simpleFileContainer.close();
    }

    @Test
    void testGetSuccessful() {
        simpleFileContainer.open();

        assertEquals(simpleFileContainer.get(0L), 123);
        assertEquals(simpleFileContainer.get(1L), 456);
        assertEquals(simpleFileContainer.get(2L), 789);
        assertEquals(simpleFileContainer.get(3L), 321);
        assertEquals(simpleFileContainer.get(4L), 654);
        assertEquals(simpleFileContainer.get(5L), 987);

        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);
        assertEquals(metaData.getLongProperty(currentSizeField), 78);

        simpleFileContainer.close();
    }

    @Test
    void testGetDeletedRecord() {
        simpleFileContainer.open();

        try {
            utils.delete(3L);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(NoSuchElementException.class, () -> {
            simpleFileContainer.get(3L);
        });

        simpleFileContainer.close();
    }

    @Test
    void testInsertClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
           simpleFileContainer.insert(123);
        });
    }

    @Test
    void testInsertSuccessful() {
        simpleFileContainer.open();

        simpleFileContainer.insert(1024);
        simpleFileContainer.insert(2048);
        simpleFileContainer.insert(4096);
        try {
            assertEquals(utils.get(0L), 123);
            assertEquals(utils.get(1L), 456);
            assertEquals(utils.get(2L), 789);
            assertEquals(utils.get(3L), 321);
            assertEquals(utils.get(4L), 654);
            assertEquals(utils.get(5L), 987);
            assertEquals(utils.get(6L), 1024);
            assertEquals(utils.get(7L), 2048);
            assertEquals(utils.get(8L), 4096);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 8L);
        assertEquals(metaData.getLongProperty(currentSizeField), 120);

        simpleFileContainer.close();
    }

    @Test
    void testUpdateClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            simpleFileContainer.update(0L, 1024);
        });
    }

    @Test
    void testUpdateOutOfBound() {
        simpleFileContainer.open();

        assertThrows(NoSuchElementException.class, () -> {
           simpleFileContainer.update(9L, 1024);
        });

        simpleFileContainer.close();
    }

    @Test
    void testUpdateSuccessful() {
        simpleFileContainer.open();

        simpleFileContainer.update(0L, 321);
        simpleFileContainer.update(1L, 654);
        simpleFileContainer.update(2L, 987);

        try {
            assertEquals(utils.get(0L), 321);
            assertEquals(utils.get(1L), 654);
            assertEquals(utils.get(2L), 987);
            assertEquals(utils.get(3L), 321);
            assertEquals(utils.get(4L), 654);
            assertEquals(utils.get(5L), 987);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);
        assertEquals(metaData.getLongProperty(currentSizeField), 78);

        simpleFileContainer.close();
    }

    @Test
    void testUpdateDeletedRecord() {
        simpleFileContainer.open();

        try {
            utils.delete(3L);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(NoSuchElementException.class, () -> {
            simpleFileContainer.update(3L, 1024);
        });

        simpleFileContainer.close();
    }

    @Test
    void testRemoveClosedContainer() {
        assertThrows(IllegalStateException.class, () -> {
            simpleFileContainer.remove(0L);
        });
    }

    @Test
    void testRemoveOutOfBound() {
        simpleFileContainer.open();

        assertThrows(NoSuchElementException.class, () -> {
            simpleFileContainer.remove(6L);
        });

        simpleFileContainer.close();
    }

    @Test
    void testRemoveSuccessful() {
        simpleFileContainer.open();

        simpleFileContainer.remove(2L);
        try {
            assertEquals(utils.get(0L), 123);
            assertEquals(utils.get(1L), 456);
            assertThrows(NoSuchElementException.class, () -> {
                utils.get(2L);
            });
            assertEquals(utils.get(3L), 321);
            assertEquals(utils.get(4L), 654);
            assertEquals(utils.get(5L), 987);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty(currentKeyField), 5L);
        assertEquals(metaData.getLongProperty(currentSizeField), 78);

        simpleFileContainer.close();
    }

    @Test
    void testRemoveDeletedRecord() {
        simpleFileContainer.open();

        try {
            utils.delete(3L);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThrows(NoSuchElementException.class, () -> {
            simpleFileContainer.remove(3L);
        });

        simpleFileContainer.close();
    }
}

