package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.bmp.BMPHeader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

public abstract class LSBSplitter implements Splitter {
    @Override
    public ReadableByteChannel splitAll(BMPHeader header, ReadableByteChannel pixelChannel) throws IOException {
        var pixels = header.getImageSize() / header.getDib().getBitsPerPixel() * Byte.SIZE;
        System.out.println("Number of pixels: " + pixels);

        return getEncodedFile(pixelChannel);

    }

    protected abstract int getBufferSize();

    protected abstract byte decodeByte(byte[] raw);

    protected ReadableByteChannel getEncodedFile(ReadableByteChannel pixelChannel) throws IOException {

        var buffer = ByteBuffer.allocate(getBufferSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return new ReadableByteChannel() {

            private int readByte() throws IOException {
                var read = pixelChannel.read(buffer);
                buffer.flip();
                if (read < getBufferSize()) {
                    return -1;
                }
                var raw = buffer.array();
                return decodeByte(raw);
            }

            @Override
            public int read(ByteBuffer dst) throws IOException {
                var theValue = readByte();
                if (theValue == -1) {
                    return -1;
                }
                dst.put((byte) theValue);
                return 1;
            }

            @Override
            public boolean isOpen() {
                return pixelChannel.isOpen();
            }

            @Override
            public void close() throws IOException {
                pixelChannel.close();
            }
        };
    }
}
