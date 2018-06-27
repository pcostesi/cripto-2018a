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
        buffer.order(ByteOrder.BIG_ENDIAN);

        return new ReadableByteChannel() {
            private byte[] readByte() throws IOException {
                int read;
                do {
                    read = pixelChannel.read(buffer);
                    if (read == -1) {
                        return null;
                    }
                } while (read < getBufferSize());

                buffer.flip();
                return buffer.array();
            }

            @Override
            public int read(ByteBuffer dst) throws IOException {
                var read = 0;
                while (dst.hasRemaining()) {
                    var theValue = readByte();
                    if (theValue == null && read > 0) {
                        break;
                    } else if (theValue == null && read == 0) {
                        return -1;
                    }
                    dst.put(decodeByte(theValue));
                    read++;
                }
                return read;
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
