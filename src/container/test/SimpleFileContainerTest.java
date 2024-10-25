package container.test;

import container.impl.SimpleFileContainer;
import container.test.utils.SimpleFileContainerTestUtils;
import io.IntSerializer;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import util.MetaData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder
class SimpleFileContainerTest {
    static SimpleFileContainer<Integer> simpleFileContainer;
    static SimpleFileContainerTestUtils<IntSerializer, Integer> utils;

    @BeforeEach
    void setUp() {
        Path path = Path.of("src/container/data").toAbsolutePath();
        IntSerializer intSerializer = new IntSerializer();
        utils = new SimpleFileContainerTestUtils<>(path, intSerializer);

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

        simpleFileContainer = new SimpleFileContainer<>(path, "test", intSerializer);
    }

    @AfterAll
    static void cleanUp() {
        utils.destroy();
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
        assertEquals(metaData.getLongProperty("currentKey"), 5);
        assertEquals(metaData.getLongProperty("currentSize"),78 );

        simpleFileContainer.close();
    }

    @Test
    void testGetMetaDataAfterInsert() {
        // TODO
    }

    @Test
    void testGetMetaDataAfterUpdate() {
        // TODO
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
        assertEquals(simpleFileContainer.get(6L), 1024);
        assertEquals(simpleFileContainer.get(7L), 2048);
        assertEquals(simpleFileContainer.get(8L), 4096);

        // TODO: Mock metaData using Mockito
        MetaData metaData = simpleFileContainer.getMetaData();
        assertEquals(metaData.getLongProperty("currentKey"), 8);
        assertEquals(metaData.getLongProperty("currentSize"), 120);



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

        // TODO: Mock metaData using Mockito and ensure that currentKey and currentSize are unchanged
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
        // TODO
    }
}
