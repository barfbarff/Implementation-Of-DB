package shop.storage;

public interface CustomerStoreQuery {

    /**
     * Prints each user from the store along with their orders, including all products.
     */
    void queryAllUsers();

    /**
     * Prints the most ordered product and the number of times it has been ordered.
     */
    void queryTopProduct();
}
