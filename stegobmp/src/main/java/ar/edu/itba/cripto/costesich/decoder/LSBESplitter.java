package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.bmp.BMPHeader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class LSBESplitter implements Splitter {
    @Override
    public ReadableByteChannel splitAll(BMPHeader header, ReadableByteChannel pixelChannel) throws IOException {
        var pixels = header.getImageSize() / header.getDib().getBitsPerPixel() * Byte.SIZE;
        return getEncodedFile(pixelChannel);

    }

    protected ReadableByteChannel getEncodedFile(ReadableByteChannel pixelChannel) throws IOException {
        var is = Channels.newInputStream(pixelChannel);

        return new ReadableByteChannel() {
            private int idx = 0;
            private byte secretByte = 0;
            private boolean done = false;

            @Override
            public int read(ByteBuffer dst) throws IOException {
                var read = 0;
                if (done) {
                    return -1;
                }
                while (dst.hasRemaining()) {
                    var pixel = is.read();
                    if (pixel == -1) {
                        done = true;
                        return read;
                    }
                    if (pixel == 254 || pixel == 255) {
                        secretByte = (byte) (((secretByte << 1) & 0b11111110) | (pixel & 1));
                        idx += 1;
                        if (idx >= Byte.SIZE) {
                            dst.put(secretByte);
                            read++;
                            idx = 0;
                        }
                    }
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
