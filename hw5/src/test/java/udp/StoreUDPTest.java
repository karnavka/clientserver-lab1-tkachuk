package udp;

import enums.Commands;
import org.junit.jupiter.api.*;
import packet.Message;
import packet.Package;
import packet_processing.*;
import service.ProductService;
import utils.DataBase;
import utils.Decoder;
import utils.Encoder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoreUDPTest {
    private static final int PORT = 9092;
    private static DatagramSocket serverSocket;
    private static ProductService service;

    @BeforeAll
    static void startServer() throws Exception {
        service = DataBase.getProductService();
        service.clearAll();

        new Thread(new Decryptor()).start();
        new Thread(new Processor(service)).start();
        new Thread(new Encryptor()).start();
        new Thread(new Sender()).start();

        serverSocket = new DatagramSocket(PORT);
        Thread serverThread = new Thread(new ReceiverUDP(serverSocket));
        serverThread.setDaemon(true);
        serverThread.start();

        Thread.sleep(300);
    }

    @AfterAll
    static void cleanup() {
        service.clearAll();
        serverSocket.close();
    }

    @Test @Order(1)
    void createProduct() throws Exception {
        String payload = "name: UDPUnicorn\ncategory: Fruits\nquantity: 30\nprice: 2.0";
        String response = sendAndReceive(Commands.CREATE_PRODUCT, payload);
        assertTrue(response.contains("UDPUnicorn"), "Expected product name: " + response);
    }

    @Test @Order(2)
    void readProduct() throws Exception {
        String response = sendAndReceive(Commands.READ_PRODUCT, "name: UDPUnicorn");
        assertTrue(response.contains("UDPUnicorn"), "Expected product: " + response);
    }

    @Test @Order(3)
    void addProductAmount() throws Exception {
        String response = sendAndReceive(Commands.ADD_PRODUCT_AMOUNT, "product: UDPUnicorn\namount: 20");
        assertTrue(response.contains("50"), "Expected quantity 50: " + response);
    }

    @Test @Order(4)
    void deleteProductAmount() throws Exception {
        String response = sendAndReceive(Commands.DEL_PRODUCT_AMOUNT, "product: UDPUnicorn\namount: 10");
        assertTrue(response.contains("40"), "Expected quantity 40: " + response);
    }

    @Test @Order(5)
    void getProductQuantity() throws Exception {
        String response = sendAndReceive(Commands.GET_PRODUCT_QUANTITY, "product: UDPUnicorn");
        assertTrue(response.contains("40"), "Expected quantity 40: " + response);
    }

    @Test @Order(6)
    void setPrice() throws Exception {
        String response = sendAndReceive(Commands.SET_PRICE, "product: UDPUnicorn\nprice: 4.99");
        assertTrue(response.contains("4.99"), "Expected new price: " + response);
    }

    @Test @Order(7)
    void addGroup() throws Exception {
        String response = sendAndReceive(Commands.ADD_GROUP, "group: UDPGroup");
        assertTrue(response.contains("UDPGroup"), "Expected group: " + response);
    }

    @Test @Order(8)
    void searchProducts() throws Exception {
        String response = sendAndReceive(Commands.SEARCH_PRODUCTS, "category: Fruits");
        assertTrue(response.contains("UDPUnicorn"), "Expected product in search: " + response);
    }

    @Test @Order(9)
    void deleteProduct() throws Exception {
        String response = sendAndReceive(Commands.DELETE_PRODUCT, "name: UDPUnicorn");
        assertTrue(response.contains("deleted"), "Expected deletion: " + response);
    }

    @Test @Order(10)
    void readDeletedProduct() throws Exception {
        String response = sendAndReceive(Commands.READ_PRODUCT, "name: UDPUnicorn");
        assertTrue(response.contains("not found"), "Expected not found: " + response);
    }

    private String sendAndReceive(Commands command, String payload) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            InetAddress address = InetAddress.getByName(null);

            Package request = new Package(new Message(command.cType, 1, payload));
            byte[] data = Encoder.encode(request);
            socket.send(new DatagramPacket(data, data.length, address, PORT));

            byte[] buffer = new byte[65535];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            byte[] answer = new byte[response.getLength()];
            System.arraycopy(response.getData(), 0, answer, 0, response.getLength());
            return Decoder.decode(answer).getbMsg().getMessage();
        }
    }
}
