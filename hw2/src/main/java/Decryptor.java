import packet.Package;
import utils.Decoder;
import utils.Queues;

public class Decryptor {
    public void decrypt() {
        byte[] rawData = Queues.queueOfRawInfo.poll();
        Package pg = Decoder.decode(rawData);
        Queues.queueOfPackages.add(pg);
    }
}
