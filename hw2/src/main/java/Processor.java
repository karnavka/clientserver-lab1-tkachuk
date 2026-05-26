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

    private void process() throws InterruptedException {
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
                    answer.setMessage("OK");
                }
                return;
            }
        }
        answer.setMessage("Product not found");
    }

    private void addProductsToGroup(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String[] lines = fullMessage.split("\n");
        String productName = lines[0].split(": ")[1];
        String groupName = lines[1].split(": ")[1];

        if (DataBase.groups.containsKey(groupName)) {
            ConcurrentHashMap<String, Product> group = DataBase.groups.get(groupName);
            if (group.containsKey(productName)) {
                answer.setMessage("Product already exists");
            } else {
                group.put(productName, new Product(productName));
                answer.setMessage("OK");
            }
        } else {
            answer.setMessage("Group not found");
        }

    }

    private void addGroup(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        System.out.println(fullMessage);
        String groupName = fullMessage.split(": ")[1];
        if (DataBase.groups.containsKey(groupName)) {
            answer.setMessage("Group already exists");
        } else {
            DataBase.groups.put(groupName, new ConcurrentHashMap<>());
            answer.setMessage("OK");
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
                    int currentQuantity = lock.getQuantity();
                    lock.setQuantity(lock.getQuantity() + amountToAdd);
                    answer.setMessage("OK");
                }
                return;
            }
        }
        answer.setMessage("Product not found");
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
                        answer.setMessage("Not enough product");
                    }
                    if (newQuantity > 0) {
                        lock.setQuantity(newQuantity);
                        answer.setMessage("OK");
                    }
                }
                return;
            }
        }
        answer.setMessage("Product not found");
    }

    private void getProductQuantity(Message msg, Message answer) {
        String fullMessage = msg.getMessage();
        String productName = fullMessage.split(": ")[1];
        for (ConcurrentHashMap<String, Product> group : DataBase.groups.values()) {
            if (group.containsKey(productName)) {
                Product lock = group.get(productName);
                synchronized (lock) {
                    answer.setMessage("quantity: " + lock.getQuantity());
                }
                return;
            }
        }
        answer.setMessage("Product not found");
    }

}
