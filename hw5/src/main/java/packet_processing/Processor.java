package packet_processing;

import enteties.Product;
import enums.Commands;
import packet.Message;
import packet.Package;
import service.ProductFilter;
import service.ProductService;
import utils.DataBase;
import utils.Queues;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor implements Runnable {
    private final ProductService productService;

    public Processor() {
        productService = DataBase.getProductService();
    }

    public Processor(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                process();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public void process() throws InterruptedException {
        Queues.packetSocket ps = Queues.queueOfPackages.take();
        Message msg = ps.getPackage().getbMsg();
        Message answer = processMessage(msg);

        Queues.packetSocket psres = new Queues.packetSocket(
                new Package(answer),
                ps.getSocket()
        );

        if (ps.getSocket().indicator.equals("UDP"))
            psres.setAddressAndPort(ps.getAddress(), ps.getPort());

        Queues.queueOfAnswers.put(psres);
    }

    public Message processMessage(Message msg) {
        Message answer = new Message(
                Commands.CALLBACK.cType,
                msg.getbUserId(),
                ""
        );

        Commands command = Commands.fromCType(msg.getcType());

        try {
            switch (command) {
                case GET_PRODUCT_QUANTITY:
                    getProductQuantity(msg, answer);
                    break;
                case DEL_PRODUCT_AMOUNT:
                    deleteProductAmount(msg, answer);
                    break;
                case ADD_PRODUCT_AMOUNT:
                    addProductAmount(msg, answer);
                    break;
                case ADD_GROUP:
                    addGroup(msg, answer);
                    break;
                case ADD_PRODUCT_TO_GROUP:
                    addProductToGroup(msg, answer);
                    break;
                case SET_PRICE:
                    setPrice(msg, answer);
                    break;
                case CREATE_PRODUCT:
                    createProduct(msg, answer);
                    break;
                case READ_PRODUCT:
                    readProduct(msg, answer);
                    break;
                case UPDATE_PRODUCT:
                    updateProduct(msg, answer);
                    break;
                case DELETE_PRODUCT:
                    deleteProduct(msg, answer);
                    break;
                case SEARCH_PRODUCTS:
                    searchProducts(msg, answer);
                    break;
                default:
                    answer.setMessage("Invalid command");
            }
        } catch (RuntimeException e) {
            answer.setMessage("error: " + e.getMessage());
        }

        return answer;
    }

    private void addGroup(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String group = data.get("group");

        if (productService.createCategory(group))
            answer.setMessage("add group: " + group);
        else
            answer.setMessage("group: " + group + " already exists");
    }

    private void addProductToGroup(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String productName = getProductName(data);
        String group = data.get("group");

        if (!productService.categoryExists(group)) {
            answer.setMessage("group: " + group + " not found");
            return;
        }

        if (productService.findByName(productName) != null) {
            answer.setMessage("product: " + productName + " already exists");
            return;
        }

        productService.create(new Product(productName, group, 0, 0));
        answer.setMessage("put product: " + productName + " to group: " + group);
    }

    private void addProductAmount(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String productName = getProductName(data);
        int amount = Integer.parseInt(data.get("amount"));

        Product product = productService.addQuantity(productName, amount);
        if (product == null)
            answer.setMessage("product: " + productName + " not found");
        else {
            answer.setMessage("add: " + amount + " " + productName +
                    "'s new quantity: " + product.getQuantity());
        }
    }

    private void deleteProductAmount(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String productName = getProductName(data);
        int amount = Integer.parseInt(data.get("amount"));

        Product product = productService.findByName(productName);
        if (product == null) {
            answer.setMessage("product: " + productName + " not found");
            return;
        }

        Product result = productService.subtractQuantity(productName, amount);
        if (result == null)
            answer.setMessage("not enough product to write off: " + productName);
        else {
            answer.setMessage("write off: " + amount + " " + productName +
                    "'s new quantity: " + result.getQuantity());
        }
    }

    private void getProductQuantity(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String productName = getProductName(data);
        Product product = productService.findByName(productName);

        if (product == null)
            answer.setMessage("product: " + productName + " not found");
        else
            answer.setMessage(productName + "'s quantity: " + product.getQuantity());
    }

    private void setPrice(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String productName = getProductName(data);
        double price = Double.parseDouble(data.get("price"));

        Product product = productService.setPrice(productName, price);
        if (product == null)
            answer.setMessage("product: " + productName + " not found");
        else
            answer.setMessage("set price: " + price + " for product: " + productName);
    }

    private void createProduct(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        String name = getProductName(data);
        String group = data.get("category");
        int quantity = intOrZero(data.get("quantity"));
        double price = doubleOrZero(data.get("price"));

        Product product = new Product(name, group, quantity, price);
        productService.create(product);
        answer.setMessage("created product: " + productToString(product));
    }

    private void readProduct(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        Product product = findProduct(data);

        if (product == null)
            answer.setMessage("product not found");
        else
            answer.setMessage("product: " + productToString(product));
    }

    private void updateProduct(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        Product product = findProduct(data);

        if (product == null) {
            answer.setMessage("product not found");
            return;
        }

        if (data.get("name") != null)
            product.setName(data.get("name"));

        if (data.get("category") != null)
            product.setCategory(data.get("category"));

        if (data.get("quantity") != null)
            product.setQuantity(Integer.parseInt(data.get("quantity")));

        if (data.get("price") != null)
            product.setPrice(Double.parseDouble(data.get("price")));

        productService.update(product);
        answer.setMessage("updated product: " + productToString(product));
    }

    private void deleteProduct(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        boolean deleted;

        if (data.get("id") != null)
            deleted = productService.deleteById(Long.parseLong(data.get("id")));
        else
            deleted = productService.deleteByName(getProductName(data));

        if (deleted)
            answer.setMessage("deleted product");
        else
            answer.setMessage("product not found");

    }

    private void searchProducts(Message msg, Message answer) {
        Map<String, String> data = parse(msg.getMessage());
        ProductFilter filter = new ProductFilter();

        filter.setName(data.get("name"));
        filter.setCategory(data.get("category"));
        filter.setMinQuantity(getInt(data, "quantity_min"));
        filter.setMaxQuantity(getInt(data, "quantity_max"));
        filter.setMinPrice(getDouble(data, "price_min"));
        filter.setMaxPrice(getDouble(data, "price_max"));

        if (data.get("page") != null)
            filter.setPage(Integer.parseInt(data.get("page")));

        if (data.get("size") != null)
            filter.setSize(Integer.parseInt(data.get("size")));


        List<Product> products = productService.search(filter);
        if (products.isEmpty()) {
            answer.setMessage("products not found");
            return;
        }

        StringBuilder text = new StringBuilder();
        for (Product product : products)
            text.append(productToString(product)).append("\n");

        answer.setMessage(text.toString().trim());
    }

    private Product findProduct(Map<String, String> data) {
        if (data.get("id") != null)
            return productService.findById(Long.parseLong(data.get("id")));

        return productService.findByName(getProductName(data));
    }

    private Map<String, String> parse(String text) {
        HashMap<String, String> data = new HashMap<>();
        String[] lines = text.split("\n");

        for (String line : lines) {
            String[] parts = line.split(": ", 2);
            if (parts.length == 2)
                data.put(parts[0].trim().toLowerCase(), parts[1].trim());
        }

        return data;
    }

    private String getProductName(Map<String, String> data) {
        if (data.get("product") != null)
            return data.get("product");

        return data.get("name");
    }

    private Integer getInt(Map<String, String> data, String key) {
        if (data.get(key) == null || data.get(key).isEmpty())
            return null;

        return Integer.parseInt(data.get(key));
    }

    private Double getDouble(Map<String, String> data, String key) {
        if (data.get(key) == null || data.get(key).isEmpty())
            return null;

        return Double.parseDouble(data.get(key));
    }

    private int intOrZero(String value) {
        if (value == null || value.isEmpty())
            return 0;

        return Integer.parseInt(value);
    }

    private double doubleOrZero(String value) {
        if (value == null || value.isEmpty())
            return 0;

        return Double.parseDouble(value);
    }

    private String productToString(Product product) {
        return "id: " + product.getId() +
                ", name: " + product.getName() +
                ", category: " + product.getCategory() +
                ", quantity: " + product.getQuantity() +
                ", price: " + product.getPrice();
    }
}
