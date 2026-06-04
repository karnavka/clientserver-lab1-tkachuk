package udp;

import packet_processing.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class StoreServerUDP {
    public static final int PORT = 8082;

    public static void main(String[] args) throws IOException {

        new Thread(new Decryptor()).start();
        new Thread(new Processor()).start();
        new Thread(new Encryptor()).start();
        new Thread(new Sender()).start();

        DatagramSocket serverSocket = new DatagramSocket(PORT);

        System.out.println("UDP Server started on port " + PORT);

        new Thread(
                new ReceiverUDP(serverSocket)
        ).start();
    }
}
