package utils;

import enteties.Product;
import service.ProductService;
import service.UserService;

import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    public static final ConcurrentHashMap<String,
            ConcurrentHashMap<String, Product>> groups =
            new ConcurrentHashMap<>();

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:store.db";
    private static final ProductService PRODUCT_SERVICE =
            new ProductService(DEFAULT_DB_URL);
    private static final UserService USER_SERVICE =
            new UserService(DEFAULT_DB_URL);

    static {
        // seed default users if they don't exist yet
        USER_SERVICE.createUser("admin", "admin123");
        USER_SERVICE.createUser("user", "password");
    }

    public static ProductService getProductService() {
        return PRODUCT_SERVICE;
    }

    public static UserService getUserService() {
        return USER_SERVICE;
    }
}
