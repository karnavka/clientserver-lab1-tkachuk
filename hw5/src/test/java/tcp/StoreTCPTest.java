package tcp;

import enums.Commands;
import org.junit.jupiter.api.*;
import packet.Message;
import packet.Package;
import packet_processing.*;
import service.ProductService;
import utils.DataBase;
import utils.Decoder;
import utils.Encoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoreTCPTest {
    private static final int PORT = 9091;
    private static Thread serverThread;
    private static ProductService service;

    @BeforeAll
    static void startServer() throws Exception {
        service = DataBase.getProductService();
        service.clearAll();

        new Thread(new Decryptor()).start();
        new Thread(new Processor(service)).start();
        new Thread(new Encryptor()).start();
        new Thread(new Sender()).start();

        ServerSocket serverSocket = new ServerSocket(PORT);
        serverThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new ReceiverTCP(socket)).start();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        Thread.sleep(300);
    }

    @AfterAll
    static void cleanup() {
        service.clearAll();
        serverThread.interrupt();
    }

    @Test @Order(1)
    void createProduct() throws Exception {
        String payload = "name: TCPUnicorn\ncategory: Fruits\nquantity: 50\nprice: 1.5";
        String response = sendAndReceive(Commands.CREATE_PRODUCT, payload);
        assertTrue(response.contains("TCPUnicorn"), "Expected product name in response: " + response);
    }

    @Test @Order(2)
    void readProduct() throws Exception {
        String response = sendAndReceive(Commands.READ_PRODUCT, "name: TCPUnicorn");
        assertTrue(response.contains("TCPUnicorn"), "Expected product in response: " + response);
    }

    @Test @Order(3)
    void addProductAmount() throws Exception {
        String response = sendAndReceive(Commands.ADD_PRODUCT_AMOUNT, "product: TCPUnicorn\namount: 10");
        assertTrue(response.contains("60"), "Expected new quantity 60: " + response);
    }

    @Test @Order(4)
    void deleteProductAmount() throws Exception {
        String response = sendAndReceive(Commands.DEL_PRODUCT_AMOUNT, "product: TCPUnicorn\namount: 20");
        assertTrue(response.contains("40"), "Expected new quantity 40: " + response);
    }

    @Test @Order(5)
    void getProductQuantity() throws Exception {
        String response = sendAndReceive(Commands.GET_PRODUCT_QUANTITY, "product: TCPUnicorn");
        assertTrue(response.contains("40"), "Expected quantity 40: " + response);
    }

    @Test @Order(6)
    void setPrice() throws Exception {
        String response = sendAndReceive(Commands.SET_PRICE, "product: TCPUnicorn\nprice: 3.99");
        assertTrue(response.contains("3.99"), "Expected new price: " + response);
    }

    @Test @Order(7)
    void updateProduct() throws Exception {
        String response = sendAndReceive(Commands.UPDATE_PRODUCT, "name: TCPUnicorn\nquantity: 100\nprice: 5.0");
        assertTrue(response.contains("100"), "Expected updated quantity: " + response);
    }

    @Test @Order(8)
    void addGroup() throws Exception {
        String response = sendAndReceive(Commands.ADD_GROUP, "group: Vegetables");
        assertTrue(response.contains("Vegetables"), "Expected group in response: " + response);
    }

    @Test @Order(9)
    void searchProducts() throws Exception {
        String response = sendAndReceive(Commands.SEARCH_PRODUCTS, "category: Fruits");
        assertTrue(response.contains("TCPUnicorn"), "Expected product in search: " + response);
    }

    @Test @Order(10)
    void deleteProduct() throws Exception {
        String response = sendAndReceive(Commands.DELETE_PRODUCT, "name: TCPUnicorn");
        assertTrue(response.contains("deleted"), "Expected deletion confirmation: " + response);
    }

    @Test @Order(11)
    void readDeletedProduct() throws Exception {
        String response = sendAndReceive(Commands.READ_PRODUCT, "name: TCPUnicorn");
        assertTrue(response.contains("not found"), "Expected not found: " + response);
    }

    private String sendAndReceive(Commands command, String payload) throws Exception {
        try (Socket socket = new Socket(InetAddress.getByName(null), PORT);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            Package request = new Package(new Message(command.cType, 1, payload));
            byte[] encoded = Encoder.encode(request);
            out.writeInt(encoded.length);
            out.write(encoded);
            out.flush();

            int length = in.readInt();
            byte[] responseBytes = new byte[length];
            in.readFully(responseBytes);
            return Decoder.decode(responseBytes).getbMsg().getMessage();
        }
    }
}
