import java.nio.ByteBuffer;

public class Encoder {

    public static byte[] encode(Package pg) {
        int wLen = pg.bMsg.message.getBytes().length + 8;
        byte bMagic = 0x13;
        ByteBuffer bb = ByteBuffer.allocate(16 + wLen+2);
        bb.put(bMagic);
        bb.put(pg.bSrc);
        bb.putLong(pg.bPktId);
        bb.putInt(wLen);
        short wCrc16 = Crc16.calculateCrc(bb.array(), 0, 14);
        bb.putShort(wCrc16);
        bb.putInt(pg.bMsg.cType);
        bb.putInt(pg.bMsg.bUserId);
        bb.put(pg.bMsg.message.getBytes());
        short wCrc162 = Crc16.calculateCrc(bb.array(), 16, wLen);
        System.out.println("wCrc162 = " + wCrc162);
        bb.putShort(wCrc162);
        return bb.array();
    }
}
