import packet.Package;
import utils.Decoder;

public class Decryptor {
    public void decrypt() {
        byte[] rawData = Queues.queueOfRawInfo.poll();
        Package pg = Decoder.decode(rawData);
        Queues.queueOfPackages.add(pg);
    }
}
