package shop.storage;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import shop.Customer;
import shop.Order;
import shop.Product;
import shop.utils.StringGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO: Task 2.2 c)
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
            String customerStr =
                    StringGenerator.genString(0, customer);
            System.out.println(customerStr);

            for (Order order : customer.getOrders()) {
                String orderStr =
                        StringGenerator.genString(1, order);
                System.out.println(orderStr);

                for (Product product : order.getItems()) {
                    String productStr =
                            StringGenerator.genString(2, product);
                    System.out.println(productStr);
                }
            }
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

        Optional<Map.Entry<Product, Integer>> topProductEntry = productSalesMap
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        topProductEntry.ifPresent(entry -> {
            String productStr =
                    StringGenerator.genString(0, entry.getKey());
            System.out.println(productStr + " is the best selling product");
        });
    }
}
