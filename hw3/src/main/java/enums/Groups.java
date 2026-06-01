package enums;

//це чисто для фейкової генерації пакетів
public enum Groups {
    // група для товарів без групи
    MEAT("meat"),
    SPICES("spices"),
    GROATS("groats");

    public final String name;

    Groups(String s) {
        this.name = s;
    }
}
