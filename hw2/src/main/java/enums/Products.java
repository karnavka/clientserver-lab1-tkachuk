package enums;

//це чисто для фейкової генерації пакетів
public enum Products {
    PORK("pork"),
    DOG("dog"),
    POULTRY("poultry"),
    SUGAR("sugar"),
    SALT("salt"),
    PAPER("paper"),
    WHEAT("wheat"),
    RICE("rice"),
    BUCKWHEAT("buckwheat");

    public final String name;

    Products(String pork) {
        this.name = pork;
    }
}
