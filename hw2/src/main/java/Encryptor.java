import packet.Package;
import utils.Encoder;
import utils.Queues;

public class Encryptor implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                encrypt();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void encrypt() throws InterruptedException {
        Package pg = Queues.queueOfAnswers.take();
        byte[] crypt = Encoder.encode(pg);
        Queues.queueOfEncryptedAnswers.put(crypt);
    }
}
