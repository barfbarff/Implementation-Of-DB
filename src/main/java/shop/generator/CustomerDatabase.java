package shop.generator;

import shop.Customer;
import shop.Product;

import java.util.ArrayList;
import java.util.List;

public class CustomerDatabase {

    private List<Product> productList;
    private List<Customer> customersList;

    public CustomerDatabase(int customerSize, int maxOrderSize, int productSize) {
        productList = ShopGenerator.generateProducts(productSize);
        customersList = new ArrayList<>();
        for (int i = 0; i < customerSize; i++) {
             customersList.add(ShopGenerator.generateCustomer(productList, maxOrderSize));
        }
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<Customer> getCustomersList() {
        return customersList;
    }
}
