package shop.example;

import shop.Customer;
import shop.generator.CustomerDatabase;
import shop.storage.H2StoreImpl;
import shop.utils.Printer;

public class H2Store {
    public static void main(String[] args) {
        CustomerDatabase customerDatabase = new CustomerDatabase(3, 3, 3);

        H2StoreImpl h2Store = new H2StoreImpl();

        h2Store.cleanUp();

        h2Store.open();

        for (Customer customer : customerDatabase.getCustomersList()) {
            h2Store.insertCustomer(customer);
        }

        Printer.printTitle("Query From The DB");

        h2Store.queryAllUsers();

        Printer.printTitle("Query Top Product");

        h2Store.queryTopProduct();

        h2Store.close();
    }
}
