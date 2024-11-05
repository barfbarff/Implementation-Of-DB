package shop.example;

import shop.Customer;
import shop.generator.CustomerDatabase;
import shop.storage.KVStoreImpl;

public class KVStore {
    public static void main(String[] args) {
        CustomerDatabase customerDatabase = new CustomerDatabase(3, 3, 3);
        KVStoreImpl kvStore = new KVStoreImpl();

        kvStore.cleanUp();

        kvStore.open();

        for (Customer customer : customerDatabase.getCustomersList()) {
            kvStore.insertCustomer(customer);
        }

        kvStore.queryAllUsers();

        kvStore.queryTopProduct();

        kvStore.close();
    }
}
