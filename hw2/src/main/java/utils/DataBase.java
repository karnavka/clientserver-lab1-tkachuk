package utils;

import enteties.Group;
import enums.Groups;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    public static List<Group> products = new ArrayList<>();

    public DataBase() {
        products.add(new Group(Groups.NO_GROUP.name));
    }
}
