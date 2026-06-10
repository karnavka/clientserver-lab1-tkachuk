package utils;

import enteties.Product;
import service.ProductService;

import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    public static final ConcurrentHashMap<String,
            ConcurrentHashMap<String, Product>> groups =
            new ConcurrentHashMap<>();

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:store.db";
    private static final ProductService PRODUCT_SERVICE =
            new ProductService(DEFAULT_DB_URL);

    public static ProductService getProductService() {
        return PRODUCT_SERVICE;
    }
}
