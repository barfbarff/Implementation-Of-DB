package shop.storage;

import shop.Customer;
import shop.generator.CustomerDatabase;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class StorePerformanceTest {
    private static final String template = "Time Elapsed Of %s: %ss [Batch Size = %d]";

    public static void main(String[] args) {
        int customerSize = 100000;
        int maxOrderSize = 10;
        int productSize = 5;
        CustomerDatabase customerDatabase = new CustomerDatabase(customerSize, maxOrderSize, productSize);
        List<Customer> customerList = customerDatabase.getCustomersList();

        testKVStoreImpl(customerList, 100);

        testH2StoreImpl(customerList, 100);

    }

    private static void testKVStoreImpl (List<Customer> customerList, int batchSize) {
        Instant startTime = Instant.now();

        KVStoreImpl kvStore = new KVStoreImpl();

        kvStore.open();

        kvStore.cleanUp();

        insertBatch(kvStore, customerList, batchSize);

        kvStore.close();

        Instant endTime = Instant.now();

        double timeElapsed = (double) Duration.between(startTime, endTime).toMillis() / 1000;

        String measurementStr = String.format(template,
                "KVStore", timeElapsed, batchSize);
        System.out.println(measurementStr);
    }

    private static void  testH2StoreImpl(List<Customer> customerList, int batchSize) {
        Instant startTime = Instant.now();

        H2StoreImpl h2Store = new H2StoreImpl();

        h2Store.cleanUp();

        h2Store.open();

        insertBatch(h2Store, customerList, batchSize);

        h2Store.close();

        Instant endTime = Instant.now();

        double timeElapsed = (double) Duration.between(startTime, endTime).toMillis() / 1000;

        String measurementStr = String.format(template,
                "H2Store", timeElapsed, batchSize);
        System.out.println(measurementStr);
    }

    private static <T extends CustomerStore> void insertBatch(T store,
                                                              List<Customer> customerList,
                                                              int batchSize) {
        int startIndex = 0;
        while (startIndex < customerList.size()) {
            int endIndex = startIndex + batchSize;

            for (int i = startIndex; i < endIndex; i++) {
                Customer customer = customerList.get(i);
                store.insertCustomer(customer);
            }

            store.commit();

            startIndex = endIndex;
        }
    }
}
