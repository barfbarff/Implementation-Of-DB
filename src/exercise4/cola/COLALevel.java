package exercise4.cola;

import java.util.Iterator;

import xxl.core.util.Pair;

/**
 * Abstracts a level in the COLA structure.
 */
public interface COLALevel<K extends Comparable<K>, V> extends Iterable<Pair<K, V>> {
    /**
     * Searches for the given key
     *
     * @param key the key to search for
     * @return null if nothing was found, the corresponding value otherwise
     */
    V search(K key);

    /**
     * Fills this level with the given elements.
     * Note, that the elements MUST be sorted
     *
     * @param elms the elements fill this level with.
     */
    void set(Iterator<Pair<K, V>> elms);
}