package packet_processing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketWrapper {

    private final Socket socketTCP;
    private final DatagramSocket socketUDP;
    public final BlockingQueue<byte[]> queueOfRawPackages;
    public final String indicator;
    private InetAddress addressOfReceiver;
    private int portOfReceiver;

    public SocketWrapper(
            Socket socket,
            BlockingQueue<byte[]> queueOfRawPackages
    ) {
        this.socketTCP = socket;
        this.socketUDP = null;
        this.queueOfRawPackages = queueOfRawPackages;
        this.indicator = "TCP";
    }

    public SocketWrapper(DatagramSocket socket, BlockingQueue<byte[]> queueOfRawPackages){
        this.socketUDP = socket;
        this.socketTCP = null;
        this.queueOfRawPackages = queueOfRawPackages;
        this.indicator = "UDP";
    }

    public void sendTCP(byte[] data) {
        try {
            DataOutputStream out =
                    new DataOutputStream(
                            socketTCP.getOutputStream()
                    );

            out.writeInt(data.length);
            out.write(data);
            out.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] readTCP() {

        try {
            DataInputStream in =
                    new DataInputStream(
                            socketTCP.getInputStream()
                    );

            int length = in.readInt();

            byte[] data = new byte[length];

            in.readFully(data);

            return data;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendUDP(
            byte[] data,
            InetAddress address,
            int port
    ) {
        try {
            DatagramPacket packet =
                    new DatagramPacket(
                            data,
                            data.length,
                            address,
                            port
                    );

            socketUDP.send(packet);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] readUDP() {
        try {
            byte[] buffer = new byte[65535];

            DatagramPacket packet =
                    new DatagramPacket(
                            buffer,
                            buffer.length
                    );

            socketUDP.receive(packet);

            addressOfReceiver = packet.getAddress();
            portOfReceiver = packet.getPort();

            byte[] data = new byte[packet.getLength()];

            System.arraycopy(
                    packet.getData(),
                    0,
                    data,
                    0,
                    packet.getLength()
            );

            return data;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public InetAddress getAddressOfReceiver() {
        return addressOfReceiver;
    }
    public int getPortOfReceiver() {
        return portOfReceiver;
    }
}
