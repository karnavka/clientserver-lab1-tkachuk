package packet_processing;

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
        Queues.packetSocket ps = Queues.queueOfAnswers.take();
        Package pg = ps.getPackage();
        byte[] crypt = Encoder.encode(pg);
        Queues.byteSocket bs = new Queues.byteSocket(crypt, ps.getSocket());
        if (ps.getSocket().indicator.equals("UDP"))
            bs.setAddressAndPort(ps.getAddress(), ps.getPort());

        Queues.queueOfEncryptedAnswers.put(bs);
    }
}
