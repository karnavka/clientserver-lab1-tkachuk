import enteties.Product;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import service.ProductFilter;
import service.ProductService;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProductServiceTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ProductService service;

    @Before
    public void before() throws IOException {
        String url = "jdbc:sqlite:" + folder.newFile("test.db").getAbsolutePath();
        service = new ProductService(url);
    }

    @Test
    public void crudTest() {
        Product buckwheat = new Product("Buckwheat", "groats", 10, 2.5);
        service.create(buckwheat);

        Product fromDb = service.findById(buckwheat.getId());
        assertNotNull(fromDb);
        assertEquals("Buckwheat", fromDb.getName());
        assertEquals("groats", fromDb.getCategory());
        assertEquals(10, fromDb.getQuantity());
        assertEquals(2.5, fromDb.getPrice(), 0.001);

        fromDb.setQuantity(17);
        fromDb.setPrice(3.75);
        assertTrue(service.update(fromDb));

        Product updated = service.findByName("Buckwheat");
        assertEquals(17, updated.getQuantity());
        assertEquals(3.75, updated.getPrice(), 0.001);

        assertTrue(service.deleteByName("Buckwheat"));
        assertNull(service.findByName("Buckwheat"));
    }

    @Test
    public void filterTest() {
        service.create(new Product("Apple", "food", 10, 2.0));
        service.create(new Product("Banana", "food", 5, 4.0));
        service.create(new Product("Hammer", "tools", 3, 8.0));
        service.create(new Product("Apricot", "food", 20, 6.0));

        ProductFilter filter = new ProductFilter();
        filter.setName("a");
        filter.setCategory("food");
        filter.setMinPrice(3.0);
        filter.setMaxQuantity(20);
        filter.setSize(10);

        List<Product> products = service.search(filter);
        assertEquals(2, products.size());
        assertEquals("Apricot", products.get(0).getName());
        assertEquals("Banana", products.get(1).getName());

        filter.setSize(1);
        filter.setPage(1);
        List<Product> secondPage = service.search(filter);
        assertEquals(1, secondPage.size());
        assertEquals("Banana", secondPage.get(0).getName());
    }

    @Test
    public void quantityTest() {
        service.create(new Product("Rice", "groats", 2, 1.0));

        Product rice = service.addQuantity("Rice", 5);
        assertEquals(7, rice.getQuantity());

        rice = service.subtractQuantity("Rice", 3);
        assertEquals(4, rice.getQuantity());

        assertNull(service.subtractQuantity("Rice", 10));
        assertEquals(4, service.findByName("Rice").getQuantity());
        assertFalse(service.deleteByName("No product"));
    }
}
