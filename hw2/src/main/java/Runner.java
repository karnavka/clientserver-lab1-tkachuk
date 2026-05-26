import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    public static void main(String[] args) {
        int numberOfReceivers = 2;
        int numberOfDecryptors = 2;
        int numberOfProcessors = 4;
        int numberOfEncryptors = 3;
        int numberOfSenders = 5;

        ExecutorService receivers = Executors.newFixedThreadPool(numberOfReceivers);
        ExecutorService decryptors = Executors.newFixedThreadPool(numberOfDecryptors);
        ExecutorService processors = Executors.newFixedThreadPool(numberOfProcessors);
        ExecutorService encryptors = Executors.newFixedThreadPool(numberOfEncryptors);
        ExecutorService senders = Executors.newFixedThreadPool(numberOfSenders);

        for (int i = 0; i < numberOfReceivers; i++)
            receivers.execute(new FakeReceiver());

        for (int i = 0; i < numberOfDecryptors; i++)
            decryptors.execute(new Decryptor());

        for (int i = 0; i < numberOfProcessors; i++)
            processors.execute(new Processor());

        for (int i = 0; i < numberOfEncryptors; i++)
            encryptors.execute(new Encryptor());

        for (int i = 0; i < numberOfSenders; i++)
            senders.execute(new Sender());


        receivers.shutdown();
        decryptors.shutdown();
        processors.shutdown();
        encryptors.shutdown();
        senders.shutdown();
    }
}
