package udp;
import java.net.InetAddress;

public class StoreClientUDP {

    public static void main(String[] args)
            throws Exception {

        InetAddress address =
                InetAddress.getByName("localhost");

        for (int i = 0; i < 5; i++) {

            new StoreClientUDPThread(address);
        }
    }
    }

