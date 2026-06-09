package utils;

import packet.Package;
import packet_processing.SocketWrapper;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queues {
    public static final BlockingQueue<byteSocket> queueOfRawInfo = new LinkedBlockingQueue<>();
    public static final BlockingQueue<packetSocket> queueOfPackages = new LinkedBlockingQueue<>();
    public static final BlockingQueue<packetSocket> queueOfAnswers = new LinkedBlockingQueue<>();
    public static final BlockingQueue<byteSocket> queueOfEncryptedAnswers = new LinkedBlockingQueue<>();

    public static class byteSocket{
        byte[] rawData;
        SocketWrapper socket;
        InetAddress address;
        int port;
        public byteSocket(byte[] rawData, SocketWrapper socket){
            this.socket = socket;
            this.rawData = rawData;
        }
        public byte[] getRawData(){
            return rawData;
        }
        public SocketWrapper getSocket(){
            return socket;
        }
        public void setAddressAndPort(InetAddress add, int port){
            this.address = add;
            this.port = port;
        }
        public InetAddress getAddress(){
            return address;
        }
        public int getPort(){
            return port;
        }
    }

    public static class packetSocket{
        Package packet;
        SocketWrapper socket;
        InetAddress address;
        int port;
        public packetSocket(Package packet, SocketWrapper socket){
            this.socket = socket;
            this.packet = packet;
        }
        public void setAddressAndPort(InetAddress add, int port){
            address = add;
            this.port = port;
        }
        public InetAddress getAddress(){
            return address;
        }
        public int getPort(){
            return port;
        }
        public Package getPackage(){
            return packet;
        }
        public SocketWrapper getSocket(){
            return socket;
        }
    }
}
