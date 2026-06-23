package packet_processing;

import utils.Queues;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiverTCP implements Runnable {
    private final SocketWrapper socketWrapper;

    public ReceiverTCP(Socket s) throws IOException {
        socketWrapper = new SocketWrapper(s, new LinkedBlockingQueue<byte[]>());
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] packet = socketWrapper.readTCP();

                Queues.queueOfRawInfo.put(
                        new Queues.byteSocket(
                                packet,
                                socketWrapper)
                );
            }
        } catch (Exception e) {
            System.out.println("Client disconnected");
        }
    }
}
