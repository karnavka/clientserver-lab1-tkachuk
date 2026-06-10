import enums.Commands;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import packet.Message;
import packet_processing.Processor;
import service.ProductService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProcessorProductServiceIntegrationTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ProductService service;
    private Processor processor;

    @Before
    public void before() throws IOException {
        String url = "jdbc:sqlite:" + folder.newFile("processor.db").getAbsolutePath();
        service = new ProductService(url);
        processor = new Processor(service);
    }

    @Test
    public void oldCommandsTest() {
        Message answer = execute(Commands.ADD_GROUP, "group: groats");
        assertEquals("add group: groats", answer.getMessage());

        answer = execute(
                Commands.ADD_PRODUCT_TO_GROUP,
                "product: Buckwheat\ngroup: groats"
        );
        assertEquals("put product: Buckwheat to group: groats", answer.getMessage());

        execute(Commands.ADD_PRODUCT_AMOUNT, "product: Buckwheat\namount: 5");
        execute(Commands.SET_PRICE, "product: Buckwheat\nprice: 3.5");

        answer = execute(Commands.GET_PRODUCT_QUANTITY, "product: Buckwheat");
        assertEquals("Buckwheat's quantity: 5", answer.getMessage());
        assertEquals(3.5, service.findByName("Buckwheat").getPrice(), 0.001);
    }

    @Test
    public void crudCommandsTest() {
        Message answer = execute(
                Commands.CREATE_PRODUCT,
                "name: Salt\ncategory: spices\nquantity: 12\nprice: 4.5"
        );
        assertTrue(answer.getMessage().contains("created product"));

        answer = execute(Commands.READ_PRODUCT, "product: Salt");
        assertTrue(answer.getMessage().contains("name: Salt"));

        answer = execute(
                Commands.UPDATE_PRODUCT,
                "product: Salt\nquantity: 20\nprice: 5.25"
        );
        assertTrue(answer.getMessage().contains("quantity: 20"));

        answer = execute(
                Commands.SEARCH_PRODUCTS,
                "category: spices\nprice_min: 5\nquantity_max: 25\npage: 0\nsize: 10"
        );
        assertTrue(answer.getMessage().contains("name: Salt"));

        answer = execute(Commands.DELETE_PRODUCT, "product: Salt");
        assertEquals("deleted product", answer.getMessage());
    }

    private Message execute(Commands command, String body) {
        Message msg = new Message(command.cType, 1, body);
        return processor.processMessage(msg);
    }
}
