package packet;

import enums.Commands;

import java.util.concurrent.atomic.AtomicLong;

public class Package {
    private static final AtomicLong counter = new AtomicLong(0);
    private long bPktId;
    private byte bSrc;
    private Message bMsg;

    public Package() {
    }

    public Package(byte bSrc, Message bMsg) {
        this.bSrc = bSrc;
        this.bMsg = bMsg;

        bPktId = counter.incrementAndGet();
    }

    public Package(Message bMsg) {
        this((byte) 12, bMsg);
    }

    public byte getbSrc() {
        return bSrc;
    }

    public void setbSrc(byte bSrc) {
        this.bSrc = bSrc;
    }

    public long getbPktId() {
        return bPktId;
    }

    public void setbPktId(long bPktId) {
        this.bPktId = bPktId;
    }

    public Message getbMsg() {
        return bMsg;
    }

    public void setbMsg(Message bMsg) {
        this.bMsg = bMsg;
    }

    @Override
    public String toString() {
        Commands commands = Commands.values()[bMsg.getcType()];
        return "Package " + bPktId + "\n" + commands.name() + "\n" + bMsg.getMessage();
    }
}
