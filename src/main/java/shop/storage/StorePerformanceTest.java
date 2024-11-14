package shop.storage;

import shop.Customer;
import shop.generator.CustomerDatabase;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class StorePerformanceTest {
    private static final String template = "Time Elapsed Of %s: %ss";

    public static void main(String[] args) {
        int customerSize = 10000;
        int maxOrderSize = 10;
        int productSize = 5;
        CustomerDatabase customerDatabase = new CustomerDatabase(customerSize, maxOrderSize, productSize);
        List<Customer> customerList = customerDatabase.getCustomersList();

        testKVStoreImpl(customerList);

        testH2StoreImpl(customerList);
    }

    private static void testKVStoreImpl (List<Customer> customerList) {
        Instant startTime = Instant.now();

        KVStoreImpl kvStore = new KVStoreImpl();

        kvStore.open();

        kvStore.cleanUp();

        for (Customer customer : customerList) {
            kvStore.insertCustomer(customer);
        }

        kvStore.close();

        Instant endTime = Instant.now();

        double timeElapsed = (double) Duration.between(startTime, endTime).toMillis() / 1000;

        String measurementStr = String.format(template,
                "KVStore", timeElapsed);
        System.out.println(measurementStr);
    }

    private static void  testH2StoreImpl(List<Customer> customerList) {
        Instant startTime = Instant.now();

        H2StoreImpl h2Store = new H2StoreImpl();

        h2Store.cleanUp();

        h2Store.open();

        for (Customer customer : customerList) {
            h2Store.insertCustomer(customer);
        }

        h2Store.close();

        Instant endTime = Instant.now();

        double timeElapsed = (double) Duration.between(startTime, endTime).toMillis() / 1000;

        String measurementStr = String.format(template,
                "H2Store", timeElapsed);
        System.out.println(measurementStr);
    }
}
