package exercise4.cola;

import java.util.ArrayList;

import xxl.core.util.Pair;

/**
 * Models a COLA block.
 */
public class COLABlock<K, V> {

    /**
     * Stores the values of this COLABlock
     */
    private final ArrayList<Pair<K, V>> elements;

    /**
     * Creates a new COLABlock for the given size.
     *
     * @param size The size of the block
     */
    public COLABlock(int size) {
        elements = new ArrayList<>(size);
    }

    /**
     * Returns the element at the given index.
     *
     * @param index
     * @return
     */
    public Pair<K, V> get(int index) {
        return elements.get(index);
    }

    /**
     * Sets the element at the given index.
     *
     * @param index   The index the element should be stored at
     * @param element The element
     */
    public void set(int index, Pair<K, V> element) {
        if (elements.size() <= index)
            elements.add(index, element);
        else
            elements.set(index, element);
    }

    /**
     * Returns the size of the block.
     *
     * @return The size
     */
    public int getSize() {
        return elements.size();
    }
}