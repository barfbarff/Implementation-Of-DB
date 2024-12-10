package exercise4.cola;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import xxl.core.collections.containers.Container;
import xxl.core.collections.containers.io.ConverterContainer;
import xxl.core.cursors.unions.Merger;
import xxl.core.functions.Constant;
import xxl.core.io.converters.Converter;
import xxl.core.util.Pair;

/**
 * Implementation of the basic COLA index structure.
 */
public class BasicCOLA<K extends Comparable<K>, V> {

    /**
     * The main memory cache
     */
    private ArrayList<COLABlock<K, V>> cache;

    /**
     * Stores the block-offset of each array within the container
     */
    private ArrayList<Long> arrayOffsets;

    /**
     * Stores a flag for filling state of each array
     */
    private ArrayList<Boolean> filled;

    /**
     * Underlying container
     */
    private Container container;

    /**
     * Size of an element in bytes stored in the COLA blocks
     */
    private int elementSize;

    /**
     * Size of each disk block in bytes
     */
    public static int DISK_BLOCK_SIZE = 32; // in real applications this would be larger, e.g. 4096

    /**
     * Number of elements per COLA block
     */
    private int elementsPerBlock;

    /**
     * Stores the number of levels in the cache.
     * The maximum level in the cache is the largest array that is smaller or equal to block size
     */
    private int levelsInCache;

    private static boolean isPowerOfTwo(int x) {
        if (x <= 0)
            return false;

        return (x & (x - 1)) == 0;
    }

    /**
     * Creates a new basic COLA index structure with an underlying container and the given element size in bytes.
     *
     * @param elementSize    The size of each element in bytes
     * @param keyConverter   Converter for the keys
     * @param valueConverter Converter for the values
     * @param rawContainer   An underlying container (e.g. BlockFileContainer)
     */
    public BasicCOLA(int elementSize, final Converter<K> keyConverter, final Converter<V> valueConverter, Container rawContainer) {
        if(elementSize > DISK_BLOCK_SIZE || !isPowerOfTwo(elementSize))
            throw new IllegalArgumentException("elementSize must be smaller than DISK_BLOCK_SIZE and " +
                    "elementSize must be a power of two");

        this.arrayOffsets = new ArrayList<>();
        this.filled = new ArrayList<>();
        this.elementSize = elementSize;
        this.elementsPerBlock = DISK_BLOCK_SIZE / elementSize;
        this.levelsInCache = (int) (Math.log(elementsPerBlock) / Math.log(2));
        this.cache = new ArrayList<>(levelsInCache);

        this.container = new ConverterContainer(rawContainer, new Converter<COLABlock<K, V>>() {
            @Override
            public COLABlock<K, V> read(DataInput dataInput, COLABlock<K, V> block) throws IOException {
                block = new COLABlock<>(elementsPerBlock);
                // TODO: read from dataInput to construct a COLABlock

                return block;
            }

            @Override
            public void write(DataOutput dataOutput, COLABlock<K, V> object) throws IOException {
                // TODO: write a COLABlock to dataOutput

            }
        });
    }

    private COLALevel<K, V> getLevel(int level) {
        if (level >= levelsInCache) {
            // Reserve new blocks if required
            if (level >= arrayOffsets.size()) {
                Long id = reserveArray(level);
                arrayOffsets.add(level, id);
                filled.add(Boolean.FALSE);
            }
            return new DiskLevel<>(arrayOffsets.get(level), level, elementsPerBlock, container);
        } else {
            int blockIndex;
            int begin;

            if (1 << level == elementsPerBlock) {
                blockIndex = 1;
                begin = 0;
            } else {
                blockIndex = 0;
                begin = (1 << level) - 1;
            }

            if (cache.size() <= blockIndex) {
                // New block for cache
                Long id = reserveArray(level);
                arrayOffsets.add(level, id);
                filled.add(Boolean.FALSE);
                cache.add(new COLABlock<>(elementsPerBlock));
            } else if (level >= filled.size()) {
                // New level in existing block
                // Take id of previous array (same block)
                arrayOffsets.add(level, arrayOffsets.get(level - 1));
                filled.add(Boolean.FALSE);
            }

            return new CacheLevel<>(cache.get(blockIndex), begin, begin + (1 << level));
        }
    }

    /**
     * Inserts a new element into this COLA
     *
     * @param key   The key of the element
     * @param value The value of the element
     */
    public void insertElement(K key, V value) {
        List<Iterator<Pair<K, V>>> levelsToMerge = new ArrayList<>();

        // Single item Iterator for the new element
        levelsToMerge.add(new Iterator<>() {
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Pair<K, V> next() {
                hasNext = false;
                return new Pair<>(key, value);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        });

        // Find first empty level and add iterators
        int insertLevel;
        for (insertLevel = 0; insertLevel < filled.size() && filled.get(insertLevel); insertLevel++) {
            levelsToMerge.add(getLevel(insertLevel).iterator());
            filled.set(insertLevel, Boolean.FALSE);
        }

        Iterator<Pair<K, V>>[] ma = levelsToMerge.toArray(new Iterator[levelsToMerge.size()]);
        Merger<Pair<K, V>> merger = new Merger<>(Comparator.comparing(Pair::getFirst), ma);

        // Insert in new level
        getLevel(insertLevel).set(merger);
        filled.set(insertLevel, Boolean.TRUE);
    }

    /**
     * Searches for the value with the given key. If there is no such
     * value, an exception is thrown.
     *
     * @param key The key for the requested value
     * @return The value for the key if existing
     * @throws NoSuchElementException If there is no such value
     */
    public V searchElement(K key) throws NoSuchElementException {
        // TODO: impl. top to bottom search

        throw new NoSuchElementException("No Entry found for Key: " + key);
    }

    /**
     * Writes the cached arrays to disk.
     */
    public void close() {
        for (int i = 0; i < cache.size(); i++) {
            container.update(i * DISK_BLOCK_SIZE, cache.get(i));
        }
    }

    /**
     * Reads the cached arrays from disk.
     */
    public void open() {
        for (int i = 0; i < cache.size(); i++) {
            cache.set(i, (COLABlock<K, V>) container.get(i * DISK_BLOCK_SIZE));
        }
    }

    /**
     * Reserves memory in the container for the new array and returns the
     * ID of the first block representing the array. All other blocks have
     * consecutive IDs.
     *
     * @param level The level of the array
     * @return The id of the first COLABlock
     */
    private Long reserveArray(int level) {
        if (level >= levelsInCache) {
            Long id = (Long) container.reserve(new Constant(null));
            long arrayLength = 1L << level;
            long blockCount = arrayLength / elementsPerBlock;

            for (int i = 1; i < blockCount; i++)
                container.reserve(new Constant(null));

            return id;
        } else {
            return (Long) container.reserve(new Constant(null));
        }
    }

    public void printLevels() {
        for (int i = 0; i < arrayOffsets.size(); i++) {
            if (filled.get(i))
                System.out.println(i + ": " + getLevel(i));
            else
                System.out.println(i + ": EMPTY");
        }
        System.out.println("=============================================");
    }
}