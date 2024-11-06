package shop.example;

import shop.Customer;
import shop.generator.CustomerDatabase;
import shop.storage.KVStoreImpl;
import shop.utils.Printer;

public class KVStore {
    public static void main(String[] args) {
        CustomerDatabase customerDatabase = new CustomerDatabase(3, 3, 3);

        Printer.printTitle("Generated Data");

        for (Customer customer : customerDatabase.getCustomersList()) {
            Printer.print(0, customer);
        }

        KVStoreImpl kvStore = new KVStoreImpl();


        kvStore.open();

        for (Customer customer : customerDatabase.getCustomersList()) {
            kvStore.insertCustomer(customer);
        }

        Printer.printTitle("Query From The DB");

        kvStore.queryAllUsers();

        kvStore.queryTopProduct();

        kvStore.cleanUp();

        kvStore.close();
    }
}
