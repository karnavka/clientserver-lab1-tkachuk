import enteties.Group;
import enums.Groups;

import java.util.ArrayList;
import java.util.List;

public class DB {
    public static List<Group> products = new ArrayList<>();
   public DB (){
     products.add(new Group(Groups.NO_GROUP.name));
    }
}
