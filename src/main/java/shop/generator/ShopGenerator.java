package shop.generator;

import shop.Customer;
import shop.Order;
import shop.Product;
import shop.utils.RandomGenerator;
import shop.utils.RandomIntGenerator;
import shop.utils.RandomStrGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ShopGenerator {

    private final static Random random = new Random();
    private final static RandomGenerator<Integer> productIdGenerator =
            new RandomIntGenerator();
    private final static RandomGenerator<Integer> orderIdGenerator =
            new RandomIntGenerator();
    private final static RandomGenerator<Integer> customerIdGenerator =
            new RandomIntGenerator();
    private final static RandomGenerator<String> randomStrGenerator =
            new RandomStrGenerator();

    public static List<Product> generateProducts(int numProducts) {
        List<Product> productList = new ArrayList<>();

        for (int i = 0; i < numProducts; i++) {
            int randomId = productIdGenerator.generate();
            String randomName = randomStrGenerator.generate();
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
            int randomId = orderIdGenerator.generate();
            String randomAddress = randomStrGenerator.generate();

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

        int randomId = customerIdGenerator.generate();
        String randomName = randomStrGenerator.generate();
        String randomAddress = randomStrGenerator.generate();

        Customer customer = new Customer(randomId, randomName, randomAddress);
        for (Order order : orderList) {
            customer.addOrder(order);
        }

        return customer;
    }
}
