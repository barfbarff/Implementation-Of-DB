package shop;

import java.util.ArrayList;
import java.util.List;

public class Customer implements java.io.Serializable {

    /*
     * customerId should be unique for every new customer!
     */
    private Integer customerId;
    private String userName;
    private String address;
    /*
     * List containing all orders the customer ever made.
     * For this exercise there should be at least one order for every randomly generated customer!
     */
    private List<Order> orders = new ArrayList<>();

    public Customer(Integer customerId, String userName, String address) {
        this.customerId = customerId;
        this.userName = userName;
        this.address = address;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order){
        orders.add(order);
    }
}
