package shop.utils;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.util.List;

public class Printer {
    public static void printCustomer(int prefixTabs, Customer customer) {
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

    public static void printTopProducts(List<Product> topProducts, int orderCount) {
        if (topProducts.size() > 1) {
            String titleStr = "The following products are best-selling products,"
                    + " they have been ordered " + orderCount + " times.";
            System.out.println(titleStr);
        }

        for (Product product : topProducts) {
            String productStr =
                    StringGenerator.genString(0, product);

            String printProductStr;

            if (topProducts.size() == 1) {
                printProductStr = productStr + " is the best-selling product,"
                        + " it has been ordered " + orderCount + " times.";
            } else {
                printProductStr = productStr;
            }

            System.out.println(printProductStr);
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
