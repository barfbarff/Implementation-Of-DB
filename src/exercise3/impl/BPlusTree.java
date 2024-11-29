package exercise3.impl;

import java.util.*;

public class BPlusTree<K extends Comparable<? super K>, V> {
    static final int ENTRY_COUNT = 8;

    final class Record implements Comparable<Map.Entry<K, V>>, Map.Entry<K, V> {
        final K key;
        V value;

        Record(K key) {
            this.key = key;
        }

        @Override
        public int compareTo(Map.Entry<K, V> o) {
            return this.key.compareTo(o.getKey());
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }

    abstract static class Node {
        abstract Node split();

        abstract boolean isLeaf();
    }

    final class IndexNode extends Node {
        final List<K> keys = new ArrayList<>(ENTRY_COUNT);
        final List<Node> children = new ArrayList<>(ENTRY_COUNT + 1);

        boolean isLeaf() {
            return false;
        }

        @Override
        Node split() {
            IndexNode siblingNode = new IndexNode();

            int medianKey = keys.size() / 2;

            siblingNode.keys.addAll(keys.subList(medianKey + 1, keys.size()));
            siblingNode.children.addAll(children.subList(medianKey + 1, children.size()));

            keys.subList(medianKey, keys.size()).clear();
            children.subList(medianKey + 1, children.size()).clear();

            return siblingNode;
        }
    }

    final class LeafNode extends Node {
        final List<Record> records = new ArrayList<>(ENTRY_COUNT);
        LeafNode next;

        @Override
        Node split() {
            LeafNode siblingNode = new LeafNode();

            int medianKey = records.size() / 2;

            siblingNode.records.addAll(records.subList(medianKey, records.size()));
            records.subList(medianKey, records.size()).clear();

            siblingNode.next = this.next;
            this.next = siblingNode;

            return siblingNode;
        }

        @Override
        boolean isLeaf() {
            return true;
        }
    }

    final class SplitResult {
        K promotedKey;
        Node newSibling;
        V oldValue;

        SplitResult(K promotedKey, Node newSibling, V oldValue) {
            this.promotedKey = promotedKey;
            this.newSibling = newSibling;
            this.oldValue = oldValue;
        }
    }

    private Node root;

    public BPlusTree() {
        this.root = new LeafNode();
    }

    public V insert(K key, V value) {
        SplitResult splitResult = insertRecursive(root, key, value);

        if (splitResult != null && splitResult.promotedKey == null && splitResult.newSibling == null) {
            return splitResult.oldValue;
        }

        if (splitResult != null) {
            IndexNode newRoot = new IndexNode();
            newRoot.keys.add(splitResult.promotedKey);
            newRoot.children.add(root);
            newRoot.children.add(splitResult.newSibling);
            root = newRoot;
        }

        return splitResult != null ? splitResult.oldValue : null;
    }


    @SuppressWarnings("unchecked")
    private SplitResult insertRecursive(Node node, K key, V value) {
        return node.isLeaf()
                ? insertIntoLeaf((LeafNode) node, key, value)
                : insertIntoIndex((IndexNode) node, key, value);
    }

    @SuppressWarnings("unchecked")
    private SplitResult insertIntoLeaf(LeafNode leaf, K key, V value) {
        int pos = 0;
        while (pos < leaf.records.size() && key.compareTo(leaf.records.get(pos).key) > 0) {
            pos++;
        }

        if (pos < leaf.records.size() && key.equals(leaf.records.get(pos).key)) {
            V oldValue = leaf.records.get(pos).value;
            leaf.records.get(pos).value = value;
            return new SplitResult(null, null, oldValue);
        }

        Record newRecord = new Record(key);
        newRecord.setValue(value);
        leaf.records.add(pos, newRecord);

        if (leaf.records.size() > ENTRY_COUNT) {
            LeafNode sibling = (LeafNode) leaf.split();
            return new SplitResult(sibling.records.getFirst().key, sibling, null);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private SplitResult insertIntoIndex(IndexNode indexNode, K key, V value) {
        int pos = 0;
        while (pos < indexNode.keys.size() && key.compareTo(indexNode.keys.get(pos)) >= 0) {
            pos++;
        }

        SplitResult splitResult = insertRecursive(indexNode.children.get(pos), key, value);

        if (splitResult == null) {
            return null;
        }

        if (splitResult.oldValue != null) {
            return splitResult;
        }

        indexNode.keys.add(pos, splitResult.promotedKey);
        indexNode.children.add(pos + 1, splitResult.newSibling);

        if (indexNode.keys.size() > ENTRY_COUNT) {
            K promotedKey = indexNode.keys.get(indexNode.keys.size() / 2);
            IndexNode sibling = (IndexNode) indexNode.split();
            return new SplitResult(promotedKey, sibling, null);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public V pointQuery(K key) {
        Node currentNode = root;

        while (!currentNode.isLeaf()) {
            IndexNode indexNode = (IndexNode) currentNode;

            int i = 0;
            while (i < indexNode.keys.size() && key.compareTo(indexNode.keys.get(i)) >= 0) {
                i++;
            }
            currentNode = indexNode.children.get(i);
        }

        LeafNode leaf = (LeafNode) currentNode;
        for (Record record : leaf.records) {
            if (record.key.equals(key)) {
                return record.value;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public List<? extends Map.Entry<K, V>> rangeQuery(K minKey, K maxKey) {
        List<Map.Entry<K,V>> result = new ArrayList<>();
        Node currentNode = root;

        while (!currentNode.isLeaf()) {
            IndexNode indexNode = (IndexNode) currentNode;

            int i = 0;
            while (i < indexNode.keys.size() && minKey.compareTo(indexNode.keys.get(i)) >= 0) {
                i++;
            }
            currentNode = indexNode.children.get(i);
        }

        LeafNode leafNode = (LeafNode) currentNode;
        while (leafNode != null) {
            for (Record record : leafNode.records) {
                if (record.key.compareTo(minKey) >= 0 && record.key.compareTo(maxKey) <= 0) {
                    result.add(new AbstractMap.SimpleImmutableEntry<>(record.key, record.value));
                }
            }

            if (leafNode.records.getLast().key.compareTo(maxKey) > 0) {
                break;
            }

            leafNode = leafNode.next;
        }

        return List.copyOf(result);
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, List<List<K>>> getLevelToKeysMap() {
        Map<Integer, List<List<K>>> levelToKeysMap = new HashMap<>();
        if (root == null) {
            return levelToKeysMap;
        }

        Queue<Map.Entry<Node, Integer>> queue = new LinkedList<>();
        queue.add(new AbstractMap.SimpleEntry<>(root, 0));

        while (!queue.isEmpty()) {
            Map.Entry<Node, Integer> entry = queue.poll();
            Node currentNode = entry.getKey();
            int level = entry.getValue();

            List<K> keys = currentNode.isLeaf()
                    ? ((LeafNode) currentNode).records.stream().map(record -> record.key).toList()
                    : ((IndexNode) currentNode).keys;

            levelToKeysMap.putIfAbsent(level, new ArrayList<>());
            levelToKeysMap.get(level).add(keys);

            if (!currentNode.isLeaf()) {
                IndexNode indexNode = (IndexNode) currentNode;
                for (Node child : indexNode.children) {
                    queue.add(new AbstractMap.SimpleEntry<>(child, level + 1));
                }
            }
        }

        return levelToKeysMap;
    }

    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>();

        for (int i = 0; i < 99; i++) {
            tree.insert(i, "" + i);

            for (int j = 0; j <= i; j++) {
                if (!tree.pointQuery(i).equals("" + i)) {
                    throw new RuntimeException("Key not found: " + j);
                }
            }

            if (tree.rangeQuery(0, i).size() != i + 1) {
                throw new RuntimeException("Range query failed at key " + i);
            }
        }

        List<String> pointQueryResults = new ArrayList<>();

        System.out.println("~~~~~ Keys at each level ~~~~~");
        Map<Integer, List<List<Integer>>> levelToKeysMap = tree.getLevelToKeysMap();
        levelToKeysMap.forEach((level, nodes) -> {
            System.out.println("Level " + level + " -> " + nodes);
        });

        System.out.println("~~~~~ Original Tree ~~~~~");
        System.out.println("Range query for keys from 0 to 30: " + tree.rangeQuery(0, 30));

        for (int i = 0; i <= 30; i++) {
            pointQueryResults.add(tree.pointQuery(i));
        }
        System.out.println("Point query for keys from 0 to 30: " + pointQueryResults);
        pointQueryResults.clear();

        List<String> previousValues = new ArrayList<>();

        System.out.println("~~~~~ Updated Tree: Insert new values for keys from 0 to 10 ~~~~~");
        for (int i = 0; i <= 10; i++) {
            previousValues.add(tree.insert(i, "" + (100 + i)));
        }

        System.out.println("Previous values for keys from 0 to 10: " + previousValues);

        System.out.println("Range query for keys from 0 to 30: " + tree.rangeQuery(0, 30));

        for (int i = 0; i <= 30; i++) {
            pointQueryResults.add(tree.pointQuery(i));
        }
        System.out.println("Point query for keys from 0 to 30: " + pointQueryResults);
    }
}
