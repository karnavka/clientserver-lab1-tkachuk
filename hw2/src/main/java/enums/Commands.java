package enums;

public enum Commands {
    GET_PRODUCT_QUANTITY(0),
    DEL_PRODUCT(1),
    ADD_PRODUCT(2),
    ADD_GROUP(3),
    ADD_PRODUCT_TO_GROUP(4),
    SET_PRICE(5);

    public final int cType;

    Commands(int i) {
        cType = i;
    }
}
