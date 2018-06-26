package ar.edu.itba.cripto.costesich.decoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

public class LSB4Splitter extends LSBSplitter {
    private static final int BUFFER_SIZE = 2;
    @Override
    protected int getBufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    protected byte decodeByte(byte[] raw) {
        return (byte)(raw[0] & 0b00001111 | (raw[1] & 0b00001111) << 4);
    }
}
