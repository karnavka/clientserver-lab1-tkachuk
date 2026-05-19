import java.nio.ByteBuffer;
import java.util.Arrays;

public class Decoder {
    public static Package decode(byte[] array) {
        Package pg = new Package();
        ByteBuffer bb = ByteBuffer.wrap(array);
        byte bMagic = bb.get();
        if (bMagic != 0x13) throw new IllegalArgumentException("Magic byte is not 0x13");
        byte bSrc = bb.get();
        long bPktId = bb.getLong();
        int wLen = bb.getInt();
        short wCrc16 = bb.getShort();
        short wCrc16ForChecking = Crc16.calculateCrc(bb.array(), 0, 14);
        if (wCrc16ForChecking != wCrc16) throw new IllegalArgumentException("Crc header checking failed");
        int cType = bb.getInt();
        int bUseriID = bb.getInt();
        String message = new String(bb.array(), 24, wLen - 8);
        bb.position(24 + wLen - 8);
        short wCrc16M = bb.getShort();
        short wCrc16MForChecking = Crc16.calculateCrc(bb.array(), 16, wLen);
        if (wCrc16MForChecking != wCrc16M) throw new IllegalArgumentException("Crc checking failed");


        pg.setbSrc(bSrc);
        pg.setbPktId(bPktId);
        pg.setbMsg(new Message(cType, bUseriID, message));

        return pg;
    }
}
