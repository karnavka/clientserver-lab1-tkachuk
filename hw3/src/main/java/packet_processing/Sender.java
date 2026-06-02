package packet_processing;

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
        Queues.byteSocket bs =  Queues.queueOfEncryptedAnswers.take();
        bs.getSocket().send(bs.getRawData());
    }
}
