package shop.utils;

import shop.Customer;
import shop.Order;
import shop.Product;

public class StringGenerator {
    public static String genString(int prefixTabs, Customer customer) {
        String customerTemplate = "%sCustomer [id = %d, username = %s, address = %s]";
        return String.format(customerTemplate, genTabs(prefixTabs),
                customer.getCustomerId(), customer.getUserName(), customer.getAddress());
    }

    public static String genString(int prefixTabs, Order order) {
        String orderTemplate = "%sOrder [id = %d, shippingAddress = %s]";
        return String.format(orderTemplate, genTabs(prefixTabs),
                order.getOrderId(), order.getShippingAddress());
    }

    public static String genString(int prefixTabs, Product product) {
        String productTemplate = "%sProduct [id = %d, name = %s]";
        return String.format(productTemplate, genTabs(prefixTabs),
                product.getProductId(), product.getName());
    }

    private static String genTabs(int length) {
        return "\t".repeat(length);
    }
}
