package udp;

import enums.Commands;
import enums.Groups;
import enums.Products;
import packet.Message;
import packet.Package;
import utils.Decoder;
import utils.Encoder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class StoreClientUDPThread extends Thread {

    private static final AtomicInteger THREAD_COUNT =
            new AtomicInteger(0);

    private static int ID_COUNTER = 0;

    private final int id;
    private final DatagramSocket socket;
    private final InetAddress address;
    private final Random random = new Random();

    public StoreClientUDPThread(InetAddress address)
            throws Exception {

        this.id = ID_COUNTER++;
        this.address = address;
        this.socket = new DatagramSocket();

        THREAD_COUNT.incrementAndGet();

        System.out.println(
                "Started UDP client " + id
        );

        start();
    }

    @Override
    public void run() {

        try {

            for (int i = 0; i < 3; i++) {

                Package pg = generateRandomPackage();
                System.out.println(
                        "\nClient " + id + " sent:"
                );
                if(pg.getbMsg().getMessage().trim().isEmpty()){   System.out.println(
                        "null"
                );
                }
                System.out.println(pg);

                byte[] data = Encoder.encode(pg);

                DatagramPacket packet =
                        new DatagramPacket(
                                data,
                                data.length,
                                address,
                                StoreServerUDP.PORT
                        );

                socket.send(packet);

                byte[] buffer = new byte[65535];

                DatagramPacket response =
                        new DatagramPacket(
                                buffer,
                                buffer.length
                        );

                socket.receive(response);

                byte[] answer =
                        new byte[response.getLength()];

                System.arraycopy(
                        response.getData(),
                        0,
                        answer,
                        0,
                        response.getLength()
                );

                Package resp =
                        Decoder.decode(answer);

                System.out.println(
                        "\nClient " + id + " received:"
                );
                System.out.println(resp);

      //          Thread.sleep(1000);
            }

        } catch (Exception e) {

            System.err.println(
                    "Client " + id + " error: "
                            + e.getMessage()
            );

        } finally {

            socket.close();

            THREAD_COUNT.decrementAndGet();

            System.out.println(
                    "Client " + id + " finished"
            );
        }
    }

    private Package generateRandomPackage() {
        Commands command =
                Commands.values()[random.nextInt(Commands.values().length-1)];
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

    public static int threadCount() {
        return THREAD_COUNT.get();
    }
}