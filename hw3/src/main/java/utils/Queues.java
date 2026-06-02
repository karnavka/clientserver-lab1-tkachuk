package utils;

import packet.Package;
import packet_processing.SocketWrapper;

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
    }
    public static class packetSocket{
        Package packet;
        SocketWrapper socket;
        public packetSocket(Package packet, SocketWrapper socket){
            this.socket = socket;
            this.packet = packet;
        }
        public Package getPackage(){
            return packet;
        }
        public SocketWrapper getSocket(){
            return socket;
        }
    }
}
