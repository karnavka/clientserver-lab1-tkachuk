import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class DecoderTest {
    @Test
    public void testDecode() throws DecoderException {
        Decoder decoder = new Decoder();
        Package pg = new Package((byte)0x33, 45, new Message(71, 51, "Secret")  );
        Package decoded = decoder.decode(Hex.decodeHex("1333000000000000002d0000000e227b00000047000000335365637265740000"));
        assertEquals(pg.bSrc,  decoded.bSrc);
        assertEquals(pg.bPktId,  decoded.bPktId);
        assertArrayEquals(pg.bMsg.message.getBytes(),  decoded.bMsg.message.getBytes());
        assertEquals(pg.bMsg.cType,  decoded.bMsg.cType);
        assertEquals(pg.bMsg.bUserId,  decoded.bMsg.bUserId);
    }
}