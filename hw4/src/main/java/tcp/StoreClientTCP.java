package tcp;

import java.net.InetAddress;

public class StoreClientTCP {
    static final int MAX_THREADS = 5;

    public static void main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getByName(null);
        while (!Thread.currentThread().isInterrupted()) {
            if (StoreClientTCPThread.threadCount() < MAX_THREADS)
                new StoreClientTCPThread(addr);

            Thread.sleep(500);
        }
    }
}
