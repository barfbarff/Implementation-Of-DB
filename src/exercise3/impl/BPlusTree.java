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
            this.next =siblingNode;

            return siblingNode;
        }

        @Override
        boolean isLeaf() {
            return true;
        }
    }

    private Node root;

    public BPlusTree() {
        this.root = new LeafNode();
    }

    @SuppressWarnings("unchecked")
    public V insert(K key, V value) {
        if (root.isLeaf() && ((LeafNode) root).records.size() == ENTRY_COUNT) {
            splitRoot();
        } else if (!root.isLeaf() && ((IndexNode) root).keys.size() == ENTRY_COUNT) {
            splitRoot();
        }

        return insert(root, key, value);
    }

    @SuppressWarnings("unchecked")
    private V insert(Node node, K key, V value) {
        if (node.isLeaf()) {
            LeafNode leafNode = (LeafNode) node;
            for (Record record : leafNode.records) {
                if (record.key.equals(key)) {
                    V oldValue = record.value;
                    record.value = value;
                    return oldValue;
                }
            }

            Record newRecord = new Record(key);
            newRecord.setValue(value);
            leafNode.records.add(newRecord);
            leafNode.records.sort(Comparator.naturalOrder());

            return null;
        } else {
            IndexNode indexNode = (IndexNode) node;

            for (int i = 0; i < indexNode.children.size(); i++) {
                Node childNode = indexNode.children.get(i);
                if (childNode.isLeaf() && ((LeafNode) childNode).records.size() == ENTRY_COUNT) {
                    splitChild(indexNode, i);
                } else if (!childNode.isLeaf() && ((IndexNode) childNode).keys.size() == ENTRY_COUNT) {
                    splitChild(indexNode, i);
                }
            }
            int i = 0;
            while (i < indexNode.keys.size() && key.compareTo(indexNode.keys.get(i)) >= 0) {
                i++;
            }
            return insert(indexNode.children.get(i), key, value);
        }
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
    private void splitRoot() {
        if (root.isLeaf()) {
            LeafNode siblingNode = (LeafNode) root.split();
            
            IndexNode newRoot = new IndexNode();
            newRoot.keys.add(siblingNode.records.getFirst().key);
            newRoot.children.add(root);
            newRoot.children.add(siblingNode);

            root = newRoot;
        } else {
            IndexNode siblingNode = (IndexNode) root.split();

            IndexNode newRoot = new IndexNode();
            newRoot.keys.add(siblingNode.keys.getFirst());

            newRoot.children.add(root);
            newRoot.children.add(siblingNode);

            root = newRoot;
        }
    }

    @SuppressWarnings("unchecked")
    private void splitChild(IndexNode parentNode, int childNodeIndex) {
        Node childNode = parentNode.children.get(childNodeIndex);
        Node siblingNode = childNode.split();
        K promotedKey = siblingNode.isLeaf()
                ? ((LeafNode) siblingNode).records.getFirst().key
                : ((IndexNode) siblingNode).keys.getFirst();

        parentNode.keys.add(promotedKey);
        parentNode.keys.sort(Comparator.naturalOrder());
        parentNode.children.add(childNodeIndex + 1, siblingNode);
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, List<List<K>>> getLevelToKeysMap() {
        Map<Integer, List<List<K>>> levelToKeysMap = new HashMap<>();
        if (root == null) {
            return levelToKeysMap;
        }

        Queue<Map.Entry<Node, Integer>> queue = new LinkedList<>();
        queue.add(new AbstractMap.SimpleEntry<>(root, 0)); // Add root with level 0

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
        assert false;
        BPlusTree<Integer, String> tree = new BPlusTree<>();
        for (int i = 0; i < 100; i++) {
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
        System.out.println("Range query for keys from 0 to 10: " + tree.rangeQuery(0, 10));

        for (int i = 0; i <= 10; i++) {
            pointQueryResults.add(tree.pointQuery(i));
        }
        System.out.println("Point query for keys from 0 to 10:" + pointQueryResults);
        pointQueryResults.clear();

        List<String> previousValues = new ArrayList<>();

        System.out.println("~~~~~ Updated tree ~~~~~");
        for (int i = 0; i <= 10; i++) {
            previousValues.add(tree.insert(i, "" + (100 + i)));
        }

        System.out.println("Previous values for keys from 0 to 10: " + previousValues);

        System.out.println("Range query for keys from 0 to 10: " + tree.rangeQuery(0, 10));

        for (int i = 0; i <= 10; i++) {
            pointQueryResults.add(tree.pointQuery(i));
        }
        System.out.println("Point query for keys from 0 to 10:" + pointQueryResults);
    }
}
