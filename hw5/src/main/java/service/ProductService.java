package service;

import enteties.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final String dbUrl;

    public ProductService(String dbUrl) {
        this.dbUrl = dbUrl;
        createTables();
    }

    private void createTables() {
        try (Connection con = connect();
             Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS groups_store (" +
                    "name TEXT PRIMARY KEY)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE, " +
                    "category TEXT NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "price REAL NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void clearAll() {
        try (Connection con = connect();
             Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM products");
            st.executeUpdate("DELETE FROM groups_store");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean createCategory(String groupName) {
        if (isEmpty(groupName))
            throw new IllegalArgumentException("empty group");

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT OR IGNORE INTO groups_store(name) VALUES (?)")) {
            ps.setString(1, groupName);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean categoryExists(String groupName) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT name FROM groups_store WHERE name = ?")) {
            ps.setString(1, groupName);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Product create(Product product) {
        checkProduct(product);
        createCategory(product.getCategory());

        String sql = "INSERT INTO products(name, category, quantity, price) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                product.setId(keys.getLong(1));

            return product;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Product findById(long id) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM products WHERE id = ?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return readProduct(rs);

            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Product findByName(String name) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM products WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return readProduct(rs);

            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean update(Product product) {
        checkProduct(product);
        createCategory(product.getCategory());

        String sql = "UPDATE products SET name = ?, category = ?, " +
                "quantity = ?, price = ? WHERE id = ?";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setLong(5, product.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean deleteById(long id) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM products WHERE id = ?")) {
            ps.setLong(1, id);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean deleteByName(String name) {
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM products WHERE name = ?")) {
            ps.setString(1, name);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Product addQuantity(String name, int amount) {
        Product product = findByName(name);
        if (product == null)
            return null;

        product.setQuantity(product.getQuantity() + amount);
        update(product);
        return product;
    }

    public synchronized Product subtractQuantity(String name, int amount) {
        Product product = findByName(name);
        if (product == null)
            return null;

        if (product.getQuantity() - amount < 0)
            return null;

        product.setQuantity(product.getQuantity() - amount);
        update(product);
        return product;
    }

    public synchronized Product setPrice(String name, double price) {
        Product product = findByName(name);
        if (product == null)
            return null;

        product.setPrice(price);
        update(product);
        return product;
    }

    public synchronized List<Product> search(ProductFilter filter) {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1 = 1");

        if (!isEmpty(filter.getName())) {
            sql.append(" AND name LIKE ?");
            args.add("%" + filter.getName() + "%");
        }
        if (!isEmpty(filter.getCategory())) {
            sql.append(" AND category = ?");
            args.add(filter.getCategory());
        }
        if (filter.getMinQuantity() != null) {
            sql.append(" AND quantity >= ?");
            args.add(filter.getMinQuantity());
        }
        if (filter.getMaxQuantity() != null) {
            sql.append(" AND quantity <= ?");
            args.add(filter.getMaxQuantity());
        }
        if (filter.getMinPrice() != null) {
            sql.append(" AND price >= ?");
            args.add(filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            sql.append(" AND price <= ?");
            args.add(filter.getMaxPrice());
        }

        sql.append(" ORDER BY name LIMIT ? OFFSET ?");
        args.add(filter.getSize());
        args.add(filter.getOffset());

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++)
                ps.setObject(i + 1, args.get(i));


            ResultSet rs = ps.executeQuery();
            while (rs.next())
                products.add(readProduct(rs));

            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    private Product readProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price")
        );
    }

    private void checkProduct(Product product) {
        if (product == null || isEmpty(product.getName()) || isEmpty(product.getCategory()))
            throw new IllegalArgumentException("wrong product data");

        if (product.getQuantity() < 0 || product.getPrice() < 0)
            throw new IllegalArgumentException("negative values");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
