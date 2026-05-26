public interface Receiver extends Runnable{
    void receive()  throws InterruptedException;
}
