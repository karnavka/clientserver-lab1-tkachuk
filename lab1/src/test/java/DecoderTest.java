import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class DecoderTest {
    @Test
    public void testDecode() throws DecoderException {
        Package pg = new Package((byte)0x33, 45, new Message(71, 51, "Secret")  );
        //passing an encoded hex string of the same package and decoding it
        Package decoded = Decoder.decode(Hex.decodeHex("1333000000000000002d0000000e227b0000004700000033536563726574f599"));
        assertEquals(pg.bSrc,  decoded.bSrc);
        assertEquals(pg.bPktId,  decoded.bPktId);
        assertArrayEquals(pg.bMsg.message.getBytes(),  decoded.bMsg.message.getBytes());
        assertEquals(pg.bMsg.cType,  decoded.bMsg.cType);
        assertEquals(pg.bMsg.bUserId,  decoded.bMsg.bUserId);
    }

    @Test
    public void decodeAfterEncoding() {
        Package pg = new Package((byte)0x33, 45, new Message(71, 51, "Secret")  );
        Package decoded = Decoder.decode(Encoder.encode(pg));
        assertEquals(pg.bSrc,  decoded.bSrc);
        assertEquals(pg.bPktId,  decoded.bPktId);
        assertArrayEquals(pg.bMsg.message.getBytes(),  decoded.bMsg.message.getBytes());
        assertEquals(pg.bMsg.cType,  decoded.bMsg.cType);
        assertEquals(pg.bMsg.bUserId,  decoded.bMsg.bUserId);
    }

    @Test
    public void bMagicShouldEqual0x13H()  {
        assertThrows(IllegalArgumentException.class, () ->{
           Decoder.decode(Hex.decodeHex("6733000000000000002d0000000e227b0000004700000033536563726574f599"));
        });
    }

    @Test
    public void CRC16HeaderCheck()  {
        //made changes in first 13 bytes
        assertThrows(IllegalArgumentException.class, () ->{
            Decoder.decode(Hex.decodeHex("133300000000000067670000000e227b0000004700000033536563726574f599"));
        });
    }

    @Test
    public void CRC16Check()  {
        assertThrows(IllegalArgumentException.class, () ->{
            //made some changes in bytes starting from 16th
            Decoder.decode(Hex.decodeHex("1333000000000000002d0000000e227b0000004700006767676563726574f599"));
        });
    }
}