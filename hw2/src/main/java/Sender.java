import packet.Message;
import packet.Package;
import utils.Decoder;
import utils.Queues;

public class Sender implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                send();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void send() throws InterruptedException {
        byte[] crypt = Queues.queueOfEncryptedAnswers.take();
        Package pg = Decoder.decode(crypt);
        Message msg = pg.getbMsg();
        System.out.println(msg.getMessage());
    }
}
