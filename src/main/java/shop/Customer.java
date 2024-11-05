package shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Customer customer = (Customer) obj;
        return Objects.equals(customerId, customer.customerId)
                && Objects.equals(userName, customer.userName)
                && Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, userName, address);
    }
}
