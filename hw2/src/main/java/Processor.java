import enums.Commands;
import packet.Message;
import packet.Package;
import utils.Decoder;

public class Processor {
    public void process(){
        Package pg = Queues.queueOfPackages.poll();
        Message msg = pg.getbMsg();
        int cType = msg.getcType();
        Message answer = null;
        Commands command = Commands.values()[cType];
        switch (command){
            case GET_PRODUCT_QUANTITY:
                getProductQuantity(msg, answer);
                break;
            case DEL_PRODUCT:
                deleteProduct(msg, answer);
                break;
            case ADD_PRODUCT:
                addProduct(msg, answer);
                break;
            case ADD_GROUP:
                addGroup(msg, answer);
                    break;
            case ADD_PRODUCT_TO_GROUP:
                addProductsToGroup(msg, answer);
                break;
            case SET_PRICE:
                setPrice(msg, answer);
                break;
        }
    }

    private void setPrice(Message msg, Message answer) {
    }

    private void addProductsToGroup(Message msg, Message answer) {
    }

    private void addGroup(Message msg, Message answer) {
    }

    private void addProduct(Message msg, Message answer) {
    }

    private void deleteProduct(Message msg, Message answer) {
    }

    private void getProductQuantity(Message msg, Message answer) {
    }
}
