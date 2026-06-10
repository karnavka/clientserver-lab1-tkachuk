package enums;

public enum Groups {
    MEAT("meat"),
    SPICES("spices"),
    GROATS("groats");

    public final String name;

    Groups(String s) {
        this.name = s;
    }
}
