package utils;

import packet.Package;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queues {
    public static final BlockingQueue<byte[]> queueOfRawInfo = new LinkedBlockingQueue<>();
    public static final BlockingQueue<Package> queueOfPackages = new LinkedBlockingQueue<>();
    public static final BlockingQueue<Package> queueOfAnswers = new LinkedBlockingQueue<>();
    public static final BlockingQueue<byte[]> queueOfEncryptedAnswers = new LinkedBlockingQueue<>();
}
