package fr.flavien;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class PNGFile {
    private LinkedList<PNGChunk> chunks;

    public PNGFile() {
        chunks = new LinkedList<>();
    }

    public byte[] readFile(String filePath) throws IllegalArgumentException, IOException {
        byte[] bs = Files.readAllBytes(Paths.get(filePath));

        byte[] chunkContent = null;

        if (bs.length < ByteUtil.PNGHeader.length)
            throw new IllegalArgumentException();
        for(int i = 0; i < ByteUtil.PNGHeader.length; i++) {
            System.out.println(bs[i]);
            if(bs[i] != ByteUtil.PNGHeader[i])
                throw new IllegalArgumentException();
        }

        int cursor = ByteUtil.PNGHeader.length;
        while(cursor >= 0 && cursor < bs.length) {
            PNGChunk newChunk = new PNGChunk();
            cursor = newChunk.read(bs, cursor);
            if(ByteUtil.areBytesEqualToString(newChunk.getTypeBytes(), ByteUtil.hiddenChunkName))
                chunkContent = newChunk.getDataBytes();
            chunks.addLast(newChunk);
        }

        for(PNGChunk c : chunks) {
            c.printType();
        }

        return chunkContent;
    }

    public void writeFile(String path) throws IOException {
        FileOutputStream output = new FileOutputStream(new File(path));
        // Write the PNG header
        for(int i = 0; i < ByteUtil.PNGHeader.length; i++)
            output.write(ByteUtil.PNGHeader[i]);

        // Write the PNG chunks
        for(PNGChunk c : chunks) {
            byte[] tempBytes = c.getLengthBytes();
            for(int i = 0; i < tempBytes.length; i++)
                output.write(tempBytes[i]);
            tempBytes = c.getTypeBytes();
            for(int i = 0; i < tempBytes.length; i++)
                output.write(tempBytes[i]);
            tempBytes = c.getDataBytes();
            for(int i = 0; i < tempBytes.length; i++)
                output.write(tempBytes[i]);
            tempBytes = c.getCrcBytes();
            for(int i = 0; i < tempBytes.length; i++)
                output.write(tempBytes[i]);
        }
        output.flush();
        output.close();
    }

    /**
     * @action Inserts the new chunk as the second chunk of the PNG file
     */
    public void insertChunk(byte[] type, byte[] payload) {
        if(ByteUtil.areBytesEqualToString(chunks.get(1).getTypeBytes(), ByteUtil.hiddenChunkName))
            chunks.remove(1);
        chunks.add(1, new PNGChunk(type, payload));
    }
}
