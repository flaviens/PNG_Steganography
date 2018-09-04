package fr.flavien;

public class PNGChunk {

    private int length;
    private byte[] typeBytes;
    private byte[] dataBytes;
    private int crc;

    public PNGChunk() {
    }

    public PNGChunk(byte[] type, byte[] data) {
        typeBytes = type;
        length = data.length;
        dataBytes = data;
        crc = ByteUtil.calculateCRC32(data);
    }

    /**
     * @param bs
     * @param start
     * @return the next start, except if the chunk is IEND, then return -1
     */
    public int read(byte[] bs, int start) {
        length = ByteUtil.sum4Bytes(bs, start);
        typeBytes = new byte[]{bs[start + 4], bs[start + 5], bs[start + 6], bs[start + 7]};

        dataBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            dataBytes[i] = bs[start + 8 + i];
        }
        crc = ByteUtil.sum4Bytes(bs, 8 + length);

        if (ByteUtil.isIEND(typeBytes))
            return -1;
        return start + 12 + length;
    }

    public void printType() {
        for (int i = 0; i < 4; i++)
            System.out.print((char) typeBytes[i]);
        System.out.println();
    }

    public int getLength() {
        return length;
    }

    public byte[] getTypeBytes() {
        return typeBytes;
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public byte[] getLengthBytes() {
        return ByteUtil.intToBytes(length);
    }

    public byte[] getCrcBytes() {
        return ByteUtil.intToBytes(crc);
    }
}
