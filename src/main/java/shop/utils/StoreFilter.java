package shop.utils;

import shop.Product;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreFilter {
    public static Map.Entry<List<Product>, Integer> getTopProducts(Map<Product, Integer> productSalesMap) {
        Optional<Map.Entry<Product, Integer>> topProductEntry = productSalesMap
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());



        final AtomicInteger highestOrderCount = new AtomicInteger();
        topProductEntry.ifPresent(topEntry -> {
            highestOrderCount.set(topEntry.getValue());
        });

        List<Product> topProducts = productSalesMap
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), highestOrderCount.get()))
                .map(Map.Entry::getKey)
                .toList();

        return new AbstractMap.SimpleImmutableEntry<>(topProducts, highestOrderCount.get());
    }
}
