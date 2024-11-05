package shop;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {

    /*
     * productId should be unique for every new Product!
     */
    private int productId;
    private String name;

    public Product(int productId, String name) {
        this.productId = productId;
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Product product = (Product) obj;
        return productId == product.productId
                && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name);
    }
}
