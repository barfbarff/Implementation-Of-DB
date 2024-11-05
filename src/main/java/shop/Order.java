package shop;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable {

    /*
     * orderId should be unique for every new Order!
     */
    private int orderId;
    private String shippingAddress;
    /*
     * List containing all items (Products) of an order.
     * For this exercise there should be at least one product in every order!
     */
    private List<Product> items;

    public Order(int orderId, String shippingAddress, List<Product> items) {
        this.orderId = orderId;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public List<Product> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Order order = (Order) obj;
        return orderId == order.orderId
                && Objects.equals(shippingAddress, order.shippingAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, shippingAddress);
    }
}
