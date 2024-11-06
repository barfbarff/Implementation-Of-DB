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

    public static void printTitle(String title) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("~".repeat(10));
        stringBuilder.append(" ");
        stringBuilder.append(title);
        stringBuilder.append(" ");
        stringBuilder.append("~".repeat(10));

        System.out.println(stringBuilder);
    }
}
