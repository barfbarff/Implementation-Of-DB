package shop.storage;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import shop.Customer;
import shop.Order;
import shop.Product;
import shop.utils.Printer;
import shop.utils.StoreFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KVStoreImpl implements CustomerStore, CustomerStoreQuery {
    RecordManager recordManager;

    PrimaryHashMap<Integer, Customer> customerMap;

    boolean isOpen = false;

    @Override
    public void open() {
        String dataPath = "./data/shop/KVStore/KVStore";
        String recordName = "kvStoreRecord";
        try {
            this.recordManager = RecordManagerFactory.createRecordManager(dataPath);
            customerMap = recordManager.hashMap(recordName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isOpen = true;
        }
    }

    @Override
    public void insertCustomer(Customer customer) {
        if (!isOpen) {
            throw new IllegalStateException("The connection to the DB is closed.");
        }

        customerMap.put(customer.getCustomerId(), customer);

        try {
            recordManager.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            recordManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isOpen = false;
        }
    }

    @Override
    public void cleanUp() {
        if (!isOpen) {
            throw new IllegalStateException("The connection to the DB is closed");
        }

        try {
            customerMap.clear();
            recordManager.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAllUsers() {
        if (!isOpen) {
            throw new IllegalStateException("The connection to the DB is closed");
        }

        for (Customer customer : customerMap.values()) {
            Printer.printCustomer(0, customer);
        }
    }

    @Override
    public void queryTopProduct() {
        if (!isOpen) {
            throw new IllegalStateException("The connection to the DB is closed");
        }

        Map<Product, Integer> productSalesMap = new HashMap<>();

        for (Customer customer : customerMap.values()) {
            for (Order order : customer.getOrders()) {
                for (Product product : order.getItems()) {
                    if (productSalesMap.containsKey(product)) {
                        int sales = productSalesMap.get(product) + 1;
                        productSalesMap.put(product, sales);
                    } else {
                        productSalesMap.put(product, 1);
                    }
                }
            }
        }

        Map.Entry<List<Product>, Integer> topProducts = StoreFilter
                .getTopProducts(productSalesMap);

        Printer.printTopProducts(topProducts.getKey(), topProducts.getValue());
    }
}
