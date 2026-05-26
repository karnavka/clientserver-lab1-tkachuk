package utils;

import packet.Crc16;
import packet.Package;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Encoder {
    private static final Key key =
            new SecretKeySpec(
                    "kittykittyxxxxxx".getBytes(StandardCharsets.UTF_8),
                    "AES"
            );
    static Cipher chipher;

    public static byte[] encode(Package pg) {
        try {
            chipher = Cipher.getInstance("AES");
            chipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            System.out.println("Error in Cipher init " + e.getMessage());
        }

        byte[] encryptMessage = null;
        try {
            encryptMessage = chipher.doFinal(pg.getbMsg().getMessage().getBytes());
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Error in Cipher encription: " + e.getMessage());
        }

        int wLen = encryptMessage.length + 8;
        ByteBuffer bb = ByteBuffer.allocate(16 + wLen + 2);

        bb.put((byte) 0x13);
        bb.put(pg.getbSrc());
        bb.putLong(pg.getbPktId());
        bb.putInt(wLen);
        short wCrc16 = Crc16.calculateCrc(bb.array(), 0, 14);
        bb.putShort(wCrc16);
        bb.putInt(pg.getbMsg().getcType());
        bb.putInt(pg.getbMsg().getbUserId());
        bb.put(encryptMessage);
        short wCrc162 = Crc16.calculateCrc(bb.array(), 16, wLen);
        bb.putShort(wCrc162);
        return bb.array();
    }
}
