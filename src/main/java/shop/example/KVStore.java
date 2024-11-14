package shop.example;

import shop.Customer;
import shop.generator.CustomerDatabase;
import shop.storage.KVStoreImpl;
import shop.utils.Printer;

public class KVStore {
    public static void main(String[] args) {
        CustomerDatabase customerDatabase = new CustomerDatabase(3, 3, 3);

        KVStoreImpl kvStore = new KVStoreImpl();

        kvStore.open();

        kvStore.cleanUp();

        for (Customer customer : customerDatabase.getCustomersList()) {
            kvStore.insertCustomer(customer);
        }

        Printer.printTitle("Query From The DB");

        kvStore.queryAllUsers();

        Printer.printTitle("Query Top Product");

        kvStore.queryTopProduct();

        kvStore.close();
    }
}
