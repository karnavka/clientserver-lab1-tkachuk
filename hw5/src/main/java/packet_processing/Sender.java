package packet_processing;

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
        Queues.byteSocket bs =
                Queues.queueOfEncryptedAnswers.take();

        if (bs.getSocket().indicator.equals("TCP")) {
            bs.getSocket().sendTCP(
                    bs.getRawData()
            );
        } else if (bs.getSocket().indicator.equals("UDP")) {
            bs.getSocket().sendUDP(
                    bs.getRawData(),
                    bs.getAddress(),
                    bs.getPort()
            );
        }
    }
}