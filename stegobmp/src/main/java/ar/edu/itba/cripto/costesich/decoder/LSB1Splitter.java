package ar.edu.itba.cripto.costesich.decoder;

public class LSB1Splitter extends LSBSplitter {
    private static final int BUFFER_SIZE = 8;

    @Override
    protected int getBufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    protected byte decodeByte(byte[] raw) {
        return (byte)(
                (raw[0] & 0b1) << 0 |
                (raw[1] & 0b1) << 1 |
                (raw[2] & 0b1) << 2 |
                (raw[3] & 0b1) << 3 |
                (raw[4] & 0b1) << 4 |
                (raw[5] & 0b1) << 5 |
                (raw[6] & 0b1) << 6 |
                (raw[7] & 0b1) << 7 );
    }
}
