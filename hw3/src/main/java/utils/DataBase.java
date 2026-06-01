package utils;

import enteties.Product;

import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    public static final ConcurrentHashMap<String,
            ConcurrentHashMap<String, Product>> groups =
            new ConcurrentHashMap<>();
}
