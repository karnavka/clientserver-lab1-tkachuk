package enteties;

public class Product {
    private long id;
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product(String name) {
        this(0, name, null, 0, 0);
    }

    public Product(String name, String category, int quantity, double price) {
        this(0, name, category, quantity, price);
    }

    public Product(long id, String name, String category, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "id: " + id +
                ", name: " + name +
                ", category: " + category +
                ", quantity: " + quantity +
                ", price: " + price;
    }
}

