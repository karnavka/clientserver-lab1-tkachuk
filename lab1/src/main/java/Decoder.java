import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Decoder {

    static Cipher chipher;
    private static final Key key =
            new SecretKeySpec(
                    "kittykittyxxxxxx".getBytes(StandardCharsets.UTF_8),
                    "AES"
            );
    public static Package decode(byte[] array)  {
        try
        {
            chipher = Cipher.getInstance("AES");
        }catch (NoSuchAlgorithmException | NoSuchPaddingException e ){
            System.out.println("Error in Cipher "+e.getMessage());
        }
        try {
            chipher.init(Cipher.DECRYPT_MODE, key);
        }catch (InvalidKeyException e){
            System.out.println("Error in Cipher "+e.getMessage());
        }

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
        byte[] messageBytes = new byte[wLen - 8];
        bb.get(messageBytes);

        bb.position(24 + wLen - 8);
        short wCrc16M = bb.getShort();
        short wCrc16MForChecking = Crc16.calculateCrc(bb.array(), 16, wLen);
        if (wCrc16MForChecking != wCrc16M) throw new IllegalArgumentException("Crc checking failed");

        try {
            messageBytes = chipher.doFinal(messageBytes);
        }catch(IllegalBlockSizeException| BadPaddingException e){
            System.out.println("Error in Cipher "+e.getMessage());
        }
        String message = new String(messageBytes);


        pg.setbSrc(bSrc);
        pg.setbPktId(bPktId);
        pg.setbMsg(new Message(cType, bUseriID, message));

        return pg;
    }
}
