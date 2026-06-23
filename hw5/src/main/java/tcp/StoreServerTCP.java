package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import packet_processing.*;

public class StoreServerTCP {
    static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        new Thread(new Decryptor()).start();
        new Thread(new Processor()).start();
        new Thread(new Encryptor()).start();
        new Thread(new Sender()).start();

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started...");
            while (true) {
                Socket socket = server.accept();
                try {
                    new Thread(new ReceiverTCP(socket)).start();
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}
