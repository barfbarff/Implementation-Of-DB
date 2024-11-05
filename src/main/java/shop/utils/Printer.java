package shop.utils;

import shop.Customer;
import shop.Order;
import shop.Product;

public class Printer {
    public static void print(int prefixTabs, Customer customer) {
        String customerStr =
                StringGenerator.genString(prefixTabs, customer);
        System.out.println(customerStr);

        for (Order order : customer.getOrders()) {
            String orderStr =
                    StringGenerator.genString(prefixTabs + 1, order);
            System.out.println(orderStr);

            for (Product product : order.getItems()) {
                String productStr =
                        StringGenerator.genString(prefixTabs + 2, product);
                System.out.println(productStr);
            }
        }
    }
}
