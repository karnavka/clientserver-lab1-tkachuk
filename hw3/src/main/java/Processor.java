import enteties.Product;
import enums.Commands;
import packet.Message;
import packet.Package;
import utils.DataBase;
import utils.Queues;

import java.util.concurrent.ConcurrentHashMap;

public class Processor implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                process();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public void process() throws InterruptedException {
        Package pg = Queues.queueOfPackages.take();
        Message msg = pg.getbMsg();
        int cType = msg.getcType();
        Message answer = new Message(Commands.CALLBACK.cType, msg.getbUserId(), "");
        Commands command = Commands.values()[cType];
        switch (command) {
            case GET_PRODUCT_QUANTITY:
                getProductQuantity(msg, answer);
                break;
            case DEL_PRODUCT_AMOUNT:
                deleteProduct(msg, answer);
                break;
            case ADD_PRODUCT_AMOUNT:
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
        Queues.queueOfAnswers.put(new Package(answer));
    }

    private void setPrice(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String[] lines = fullMessage.split("\n");
        String productName = lines[0].split(": ")[1];
        double price = Double.parseDouble(lines[1].split(": ")[1]);

        for (ConcurrentHashMap<String, Product> group : DataBase.groups.values()) {
            if (group.containsKey(productName)) {
                Product lock = group.get(productName);
                synchronized (lock) {
                    lock.setPrice(price);
                    answer.setMessage("set price: " + price + " for product: " + productName);
                }
                return;
            }
        }
        answer.setMessage("product: " + productName + " not found");
    }

    private void addProductsToGroup(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String[] lines = fullMessage.split("\n");
        String productName = lines[0].split(": ")[1];
        String groupName = lines[1].split(": ")[1];

        if (DataBase.groups.containsKey(groupName)) {
            ConcurrentHashMap<String, Product> group = DataBase.groups.get(groupName);
            if (group.containsKey(productName)) {
                answer.setMessage("product: " + productName + " already exists");
            } else {
                group.put(productName, new Product(productName));
                answer.setMessage("put product: " + productName + " to group: " + groupName);
            }
        } else {
            answer.setMessage("group: " + groupName + " not found");
        }

    }

    private void addGroup(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String groupName = fullMessage.split(": ")[1];
        if (DataBase.groups.containsKey(groupName)) {
            answer.setMessage("group: " + groupName + " already exists");
        } else {
            DataBase.groups.put(groupName, new ConcurrentHashMap<>());
            answer.setMessage("add group: " + groupName);
        }
    }

    private void addProduct(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String[] lines = fullMessage.split("\n");
        String productName = lines[0].split(": ")[1];
        int amountToAdd = Integer.parseInt(lines[1].split(": ")[1]);

        for (ConcurrentHashMap<String, Product> group : DataBase.groups.values()) {
            if (group.containsKey(productName)) {
                Product lock = group.get(productName);
                synchronized (lock) {
                    lock.setQuantity(lock.getQuantity() + amountToAdd);
                    answer.setMessage("add: " +amountToAdd+" "+productName + "'s new quantity: " + lock.getQuantity());
                }
                return;
            }
        }
        answer.setMessage("product: " + productName + " not found");
    }

    private void deleteProduct(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String[] lines = fullMessage.split("\n");
        String productName = lines[0].split(": ")[1];
        int amountToDelete = Integer.parseInt(lines[1].split(": ")[1]);

        for (ConcurrentHashMap<String, Product> group : DataBase.groups.values()) {
            if (group.containsKey(productName)) {
                Product lock = group.get(productName);
                synchronized (lock) {
                    int newQuantity = lock.getQuantity() - amountToDelete;
                    if (newQuantity < 0) {
                        answer.setMessage("not enough product to write off: " + productName);
                    }
                    else {
                        lock.setQuantity(newQuantity);
                        answer.setMessage("write off: " +amountToDelete+" "+ productName + "'s new quantity: " + lock.getQuantity());
                    }
                }
                return;
            }
        }
        answer.setMessage("product: " + productName + " not found");
    }

    private void getProductQuantity(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String productName = fullMessage.split(": ")[1];
        for (ConcurrentHashMap<String, Product> group : DataBase.groups.values()) {
            if (group.containsKey(productName)) {
                Product lock = group.get(productName);
                synchronized (lock) {
                    answer.setMessage(productName + "'s quantity: " + lock.getQuantity());
                }
                return;
            }
        }
        answer.setMessage("product: " + productName + " not found");
    }

}
