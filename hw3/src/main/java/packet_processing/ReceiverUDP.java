package packet_processing;

import utils.Queues;

import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiverUDP implements Runnable {

    private final SocketWrapper socketWrapper;

    public ReceiverUDP(DatagramSocket socket) {
        socketWrapper =
                new SocketWrapper(
                        socket,
                        new LinkedBlockingQueue<>()
                );
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] packet = socketWrapper.readUDP();
                Queues.queueOfRawInfo.put(
                        new Queues.byteSocket(
                                packet,
                                socketWrapper
                        )
                );
            }

        } catch (Exception e) {
            System.out.println("UDP receiver stopped");

        }
    }
}