package shop.storage;

import shop.Customer;

public interface CustomerStore {

    /**
     * Opens the storage structure (creating tables, ...)
     */
    void open();

    /**
     * Inserts the customer Object into the data structure
     * and persists the data structure to external storage
     * @param customer The customer to be inserted
     *
     */
    void insertCustomer(Customer customer);

    /**
     * If necessary closes any open resources, but does not delete the database.
     */
    void close();

    /**
     * Deletes any files associated with the database, leaving no trace of it ever existing.
     */
    void cleanUp();
}
