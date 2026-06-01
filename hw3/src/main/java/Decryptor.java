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
        byte[] rawData = Queues.queueOfRawInfo.take();
        Package pg = Decoder.decode(rawData);
        Queues.queueOfPackages.put(pg);
    }
}
