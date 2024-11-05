package shop.generator;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// TODO: Task 2.2 a)
public class ShopGenerator {
    private final static Random random = new Random();

    public static List<Product> generateProducts(int numProducts) {
        List<Product> productList = new ArrayList<>();

        for (int i = 0; i < numProducts; i++) {
            int randomId = random.nextInt(Integer.MAX_VALUE) + 1;
            String randomName = generateRandomString(10);
            productList.add(new Product(randomId, randomName));
        }

        return productList;
    }

    public static List<Order> generateOrders(List<Product> products, int numOrders) {
        List<Order> orderList = new ArrayList<>();
        List<Integer> productIndexes = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            productIndexes.add(i);
        }

        for (int i = 0; i < numOrders; i++) {
            int randomId = random.nextInt(Integer.MAX_VALUE) + 1;
            String randomAddress = generateRandomString(10);

            List<Product> randomProducts = new ArrayList<>();

            int numProducts = random.nextInt(products.size()) + 1;
            Collections.shuffle(productIndexes);
            List<Integer> randProdIndexes = productIndexes.subList(0, numProducts);

            for (Integer index : randProdIndexes) {
                randomProducts.add(products.get(index));
            }

            orderList.add(new Order(randomId, randomAddress, randomProducts));
        }

        return orderList;
    }

    public static Customer generateCustomer(List<Product> products, int maxOrders) {
        int randNumOrders = random.nextInt(maxOrders) + 1;
        List<Order> orderList = generateOrders(products, randNumOrders);

        int randomId = random.nextInt(Integer.MAX_VALUE) + 1;
        String randomName = generateRandomString(10);
        String randomAddress = generateRandomString(10);

        Customer customer = new Customer(randomId, randomName, randomAddress);
        for (Order order : orderList) {
            customer.addOrder(order);
        }

        return customer;
    }

    private static String generateRandomString(int length) {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomCharIndex = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomCharIndex));
        }
        return stringBuilder.toString();
    }
}
