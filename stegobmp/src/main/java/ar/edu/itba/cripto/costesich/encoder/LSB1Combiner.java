package ar.edu.itba.cripto.costesich.encoder;

public class LSB1Combiner extends LSBCombiner {
    private static final int BUFFER_SIZE = 8;

    protected byte[] transformByte(byte[] bytes, byte secretByte) {
        var newBytes = new byte[BUFFER_SIZE];
        newBytes[0] = (byte) ((bytes[0] & 0b11111110) | (secretByte & 0b10000000) >> 7);
        newBytes[1] = (byte) ((bytes[1] & 0b11111110) | (secretByte & 0b01000000) >> 6);
        newBytes[2] = (byte) ((bytes[2] & 0b11111110) | (secretByte & 0b00100000) >> 5);
        newBytes[3] = (byte) ((bytes[3] & 0b11111110) | (secretByte & 0b00010000) >> 4);
        newBytes[4] = (byte) ((bytes[4] & 0b11111110) | (secretByte & 0b00001000) >> 3);
        newBytes[5] = (byte) ((bytes[5] & 0b11111110) | (secretByte & 0b00000100) >> 2);
        newBytes[6] = (byte) ((bytes[6] & 0b11111110) | (secretByte & 0b00000010) >> 1);
        newBytes[7] = (byte) ((bytes[7] & 0b11111110) | (secretByte & 0b00000001) >> 0);
        return newBytes;
    }

    protected int getBufferSize() {
        return BUFFER_SIZE;
    }

}
