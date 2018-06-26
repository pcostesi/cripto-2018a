package ar.edu.itba.cripto.costesich.encoder;

public class LSB4Combiner extends LSBCombiner {
    private static final int BUFFER_SIZE = 2;

    protected void transformByte(byte[] bytes, byte secretByte) {
        bytes[0] = (byte)((bytes[0] & 0b11110000) | (secretByte & 0b00001111) >> 0);
        bytes[1] = (byte)((bytes[1] & 0b11110000) | (secretByte & 0b11110000) >> 4);
    }

    protected int getBufferSize() {
        return BUFFER_SIZE;
    }
}
