package packet_processing;

import packet.Package;
import utils.Decoder;
import utils.Queues;

public class Decryptor implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                decrypt();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void decrypt() throws InterruptedException {
        Queues.byteSocket bs = Queues.queueOfRawInfo.take();
        byte[] rawData = bs.getRawData();
        Package pg = Decoder.decode(rawData);
        Queues.packetSocket ps= new Queues.packetSocket(pg, bs.getSocket());
        Queues.queueOfPackages.put(ps);
    }
}
