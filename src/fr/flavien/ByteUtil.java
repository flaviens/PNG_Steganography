package fr.flavien;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ByteUtil {

    public static final String hiddenChunkName = "sbOj";

    public static final byte[] IEND_header = {73, 69, 78, 68};
    public static final byte[] PNGHeader = new byte[]{(byte)0x89, 80, 78, 71, 13, 10, 26, 10};

    public static int toUnsignedByte(byte b) {
        return b < 0 ? 256 + b : b;
    }

    public static boolean isIEND(byte[] bs) {
        if(bs.length != 4)
            return false;
        for(int i = 0; i < 4; i++) {
            if(bs[i] != IEND_header[i])
                return false;
        }
        return true;
    }

    public static boolean areBytesEqualToString(byte[] bs, String str) {
        if(bs.length != str.length())
            return false;
        byte[] strBytes = str.getBytes();
        for(int i = 0; i < str.length(); i++) {
            if(bs[i] != strBytes[i])
                return false;
        }
        return true;
    }

    /**
     * @return a byte representation of a in 4 bytes
     */
    public static byte[] intToBytes(int a) {
        byte[] ret = new byte[4];
        for(int i = 0; i < 4; i++) {
            ret[3-i] = (byte) (a % 256);
            a /= 256;
        }
        return ret;
    }

    /**
     * @param bs byte array, dominant byte first
     * @param start, sums 4 bytes from the byte indexed by start
     * @return
     */
    public static int sum4Bytes(byte[] bs, int start) {
        int len = 0;
        for(int i = 0; i < 4; i++) {
            len = 256 * len + ByteUtil.toUnsignedByte(bs[i+start]);
        }
        return len;
    }

    public static int calculateCRC32(byte[] bs) {
        Checksum checksum = new CRC32();
        checksum.update(bs, 0, bs.length);
        return (int) checksum.getValue();
    }
}
