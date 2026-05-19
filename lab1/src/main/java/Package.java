public class Package {
    byte bSrc;
    long bPktId;
    Message  bMsg;

    public Package(){

    }
    public Package(byte bSrc, long bPktId, Message bMsg) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bMsg = bMsg;
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
}
