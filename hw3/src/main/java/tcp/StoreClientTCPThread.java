package tcp;

import enums.Commands;
import enums.Groups;
import enums.Products;
import packet.Message;
import packet.Package;
import utils.Decoder;
import utils.Encoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreClientTCPThread extends Thread {
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger(0);
    private final int id;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    private final Random random = new Random();

    private static int ID_COUNTER = 0;

    public StoreClientTCPThread(InetAddress addr) throws IOException {
        this.id = ID_COUNTER++;
        THREAD_COUNT.incrementAndGet();
        this.socket = new Socket(addr, StoreServerTCP.PORT);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        System.out.println("Client started: " + id);

        start();
    }

    @Override
    public void run() {

        try {
            for (int i = 0; i < 10; i++) {
                Package request = generateRandomPackage();
                byte[] encoded = Encoder.encode(request);
                send(encoded);
                byte[] response = receive();
                Package responsePackage = Decoder.decode(response);
                System.out.println(
                        "Client " + id +
                                " got: " +
                                responsePackage.getbMsg().getMessage()
                );

                Thread.sleep(200);
            }

            socket.close();
            THREAD_COUNT.decrementAndGet();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void send(byte[] data) throws IOException {
        out.writeInt(data.length);
        out.write(data);
        out.flush();
    }

    private byte[] receive() throws IOException {
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);
        return data;
    }

    private Package generateRandomPackage() {
        Commands command =
                Commands.values()[random.nextInt(Commands.values().length)];
        StringBuilder bld = new StringBuilder();
        switch (command) {
            case GET_PRODUCT_QUANTITY -> {
                bld.append("product: ")
                        .append(Products.values()[random.nextInt(Products.values().length)].name);
            }
            case DEL_PRODUCT_AMOUNT, ADD_PRODUCT_AMOUNT -> {
                bld.append("product: ")
                        .append(Products.values()[random.nextInt(Products.values().length)].name);
                bld.append("\namount: ").append(random.nextInt(50) + 1);
            }
            case ADD_GROUP -> {
                bld.append("group: ")
                        .append(Groups.values()[random.nextInt(Groups.values().length)].name);
            }
            case ADD_PRODUCT_TO_GROUP -> {
                bld.append("product: ")
                        .append(Products.values()[random.nextInt(Products.values().length)].name);
                bld.append("\ngroup: ")
                        .append(Groups.values()[random.nextInt(Groups.values().length)].name);
            }
            case SET_PRICE -> {
                bld.append("product: ")
                        .append(Products.values()[random.nextInt(Products.values().length)].name);
                bld.append("\nprice: ").append(random.nextDouble());
            }
        }

        Message msg =
                new Message(
                        command.cType,
                        random.nextInt(),
                        bld.toString()
                );

        return new Package(msg);
    }

    public static int threadCount(){
        return THREAD_COUNT.get();
    }
}