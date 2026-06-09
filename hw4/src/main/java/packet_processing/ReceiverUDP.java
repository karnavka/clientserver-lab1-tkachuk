package packet_processing;

import utils.Queues;

import java.net.DatagramSocket;
import java.net.InetAddress;
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

                Queues.byteSocket bs =
                        new Queues.byteSocket(
                                packet,
                                socketWrapper
                        );
                // System.out.println("address in receiver"+socketWrapper.getAddressOfReceiver());
                //System.out.println("port in receiver"+socketWrapper.getPortOfReceiver());
                bs.setAddressAndPort(
                        socketWrapper.getAddressOfReceiver(),
                        socketWrapper.getPortOfReceiver()
                );

                Queues.queueOfRawInfo.put(bs);
            }

        } catch (Exception e) {
            System.out.println("UDP receiver stopped");

        }
    }
}