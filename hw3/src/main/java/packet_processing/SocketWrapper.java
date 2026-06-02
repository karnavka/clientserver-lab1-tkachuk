package packet_processing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SocketWrapper {

    private final Socket socket;
    public final BlockingQueue<byte[]> queueOfRawPackages;

    public SocketWrapper(
            Socket socket,
            BlockingQueue<byte[]> queueOfRawPackages
    ) {
        this.socket = socket;
        this.queueOfRawPackages = queueOfRawPackages;
    }

    public void send(byte[] data) {
        try {
            DataOutputStream out =
                    new DataOutputStream(
                            socket.getOutputStream()
                    );

            out.writeInt(data.length);
            out.write(data);
            out.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] read() {

        try {
            DataInputStream in =
                    new DataInputStream(
                            socket.getInputStream()
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
}