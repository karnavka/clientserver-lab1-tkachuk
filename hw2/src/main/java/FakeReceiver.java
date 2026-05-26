import enums.Groups;
import enums.Products;
import enums.Commands;
import packet.Message;
import packet.Package;
import tools.Encoder;

import java.util.Random;

public class FakeReceiver implements Receiver {
    private final Random random = new Random();

    @Override
    public void receive() {
        //в реальності мав прийматися масив байтів, але щоб згенерувати реальне повідомлення я
        //спочатку роблю пакет потім переводжу в масив байтів, а потім знов декриптую в пакет
        //на наступному етапі
        byte[] packet = Encoder.encode(generateRandomPackage());
    }

    private Package generateRandomPackage() {
        return new Package(generateRandomMessage());
    }

    private Message generateRandomMessage() {
        StringBuilder bld = new StringBuilder();
        int randomCommand = random.nextInt(7) - 1;
        int randomProduct = random.nextInt(9) - 1;
        Commands command = Commands.values()[randomCommand];
        Products product = Products.values()[randomProduct];
        Groups group;
        switch (command) {
            case GET_PRODUCT_QUANTITY:

            case DEL_PRODUCT:

            case ADD_PRODUCT:
                bld.append("product: ").append(product.name);
                break;
            case ADD_GROUP:
                int randomGroup = random.nextInt(4);
                group = Groups.values()[randomGroup];
                bld.append("group: ").append(group.name);
                break;
            case ADD_PRODUCT_TO_GROUP:
                group = Groups.values()[randomProduct % 3 + 1];
                bld.append("product: ").append(product.name).append("\n");
                bld.append("group: ").append(group.name);
                break;
            case SET_PRICE:
                bld.append("product: ").append(product.name).append("\n");
                bld.append("price").append(random.nextDouble(100));
                break;
        }
        return new Message(command.cType, random.nextInt(), bld.toString());
    }
}
