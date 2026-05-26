import enums.Groups;
import enums.Products;
import enums.Commands;
import packet.Message;
import packet.Package;
import utils.Encoder;
import utils.Queues;

import java.util.Random;

public class FakeReceiver implements Receiver, Runnable {
    private final Random random = new Random();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                receive();
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void receive() throws InterruptedException {
        //в реальності мав прийматися масив байтів, але щоб згенерувати реальне повідомлення я
        //спочатку роблю пакет потім переводжу в масив байтів, а потім знов декриптую в пакет
        //на наступному етапі
        byte[] packet = Encoder.encode(generateRandomPackage());
        Queues.queueOfRawInfo.add(packet);
    }

    private Package generateRandomPackage() {
        return new Package(generateRandomMessage());
    }

    private Message generateRandomMessage() {
        StringBuilder bld = new StringBuilder();
        int randomCommand = random.nextInt(6);
        Commands command = Commands.values()[randomCommand];
        int randomProduct = random.nextInt(8);
        Products product = Products.values()[randomProduct];
        Groups group;
        switch (command) {
            case GET_PRODUCT_QUANTITY:

            case DEL_PRODUCT:

            case ADD_PRODUCT:
                bld.append("product: ").append(product.name);
                bld.append("\n");
                bld.append("amount: ").append(random.nextInt(100));
                break;
            case ADD_GROUP:
                int randomGroup = random.nextInt(3);
                group = Groups.values()[randomGroup];
                bld.append("group: ").append(group.name);
                break;
            case ADD_PRODUCT_TO_GROUP:
                group = Groups.values()[randomProduct % 3];
                bld.append("product: ").append(product.name).append("\n");
                bld.append("group: ").append(group.name);
                break;
            case SET_PRICE:
                bld.append("product: ").append(product.name).append("\n");
                bld.append("price: ").append(random.nextDouble());
                break;
        }
        return new Message(command.cType, random.nextInt(), bld.toString());
    }

}
