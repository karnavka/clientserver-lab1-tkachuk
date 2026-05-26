package enteties;

import java.util.ArrayList;
import java.util.List;

public class Group {
    List<Product> products;
    String name;

    public  Group(String name) {
        products = new ArrayList<>();
        this.name = name;
    }

    public List<Product> addProduct(Product product) {
        products.add(product);
        return products;
    }
}
