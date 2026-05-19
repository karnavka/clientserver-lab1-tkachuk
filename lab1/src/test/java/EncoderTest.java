import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class EncoderTest {

    @Test
    public void sameBehaviorOfEncoders() {
Package pg = new Package((byte)0x33, 45, new Message(71, 51, "Secret")  );
     byte[]encoded = Encoder.encode(pg);
     assertEquals("1333000000000000002d00000018ecfa00000047000000332e569f8a86aa49b1e6d97f846940f0758b88", Hex.encodeHexString(encoded));
    }

    @Test
    public void samePackagesAreEncodedTheSame() {
        Package pg1 = new Package((byte)0x33, 45, new Message(100, 51, "Secret")  );
        Package pg2 = new Package((byte)0x33, 45, new Message(100, 51, "Secret")  );
        byte[]encoded1 = Encoder.encode(pg1);
        byte[]encoded2 = Encoder.encode(pg2);
        assertArrayEquals(encoded1, encoded2);
    }

    @Test
    public void differentCTypeResultsInDifferentPackageEncoding() {
        Package pg1 = new Package((byte)0x33, 45, new Message(50, 51, "Secret")  );
        Package pg2 = new Package((byte)0x33, 45, new Message(100, 51, "Secret")  );
        byte[]encoded1 = Encoder.encode(pg1);
        byte[]encoded2 = Encoder.encode(pg2);
        assertFalse(Arrays.equals(encoded1, encoded2));
    }

    @Test
    public void differentUserIDResultsInDifferentPackageEncoding() {
        Package pg1 = new Package((byte)0x33, 45, new Message(50, 0, "Secret")  );
        Package pg2 = new Package((byte)0x33, 45, new Message(50, 100, "Secret")  );
        byte[]encoded1 = Encoder.encode(pg1);
        byte[]encoded2 = Encoder.encode(pg2);
        assertFalse(Arrays.equals(encoded1, encoded2));
    }

    @Test
    public void differentMessageResultsInDifferentPackageEncoding() {
        Package pg1 = new Package((byte)0x33, 45, new Message(50, 100, "Secret1")  );
        Package pg2 = new Package((byte)0x33, 45, new Message(50, 100, "Secret2")  );
        byte[]encoded1 = Encoder.encode(pg1);
        byte[]encoded2 = Encoder.encode(pg2);
        assertFalse(Arrays.equals(encoded1, encoded2));
    }

    @Test
    public void differentBPktIdResultsInDifferentPackageEncoding() {
        Package pg1 = new Package((byte)0x33, 50, new Message(50, 100, "Secret")  );
        Package pg2 = new Package((byte)0x33, 10, new Message(50, 100, "Secret")  );
        byte[]encoded1 = Encoder.encode(pg1);
        byte[]encoded2 = Encoder.encode(pg2);
        assertFalse(Arrays.equals(encoded1, encoded2));
    }

    @Test
    public void differentBSrcResultsInDifferentPackageEncoding() {
        Package pg1 = new Package((byte)0x10, 10, new Message(50, 100, "Secret")  );
        Package pg2 = new Package((byte)0x33, 10, new Message(50, 100, "Secret")  );
        byte[]encoded1 = Encoder.encode(pg1);
        byte[]encoded2 = Encoder.encode(pg2);
        assertFalse(Arrays.equals(encoded1, encoded2));
    }
}