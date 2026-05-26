package utils;

import enteties.Group;
import enteties.Product;
import enums.Groups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    public static List<Group> products = new ArrayList<>();

    public DataBase() {
        groups.put(Groups.NO_GROUP.name, new ConcurrentHashMap<>());
    }
}
