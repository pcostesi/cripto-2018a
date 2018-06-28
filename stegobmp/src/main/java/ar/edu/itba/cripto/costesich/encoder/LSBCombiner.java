package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public abstract class LSBCombiner implements Combiner {
    private static final Logger logger = LoggerFactory.getLogger(LSBCombiner.class);

    @Override
    public ReadableByteChannel combineAll(BMPHeader header, ReadableByteChannel pixelChannel, SecretMessage secret) throws IOException {
        if (header.getImageSize() < secret.getSize() * getBufferSize()) {
            throw new IllegalArgumentException("The secret file is too large and the image too small");
        }

        return getByteStream(pixelChannel, secret.getStream(), secret.getSize());
    }

    protected abstract byte[] transformByte(byte[] bytes, byte secretByte);

    protected abstract int getBufferSize();

    private ReadableByteChannel getByteStream(ReadableByteChannel pixelChannel, InputStream secret, int fileSize) {
        var pixels = new byte[getBufferSize()];
        var pixelStream = Channels.newInputStream(pixelChannel);

        return new ReadableByteChannel() {

            @Override
            public int read(ByteBuffer dst) throws IOException {
                var secretByte = secret.read();
                if (secretByte == -1) {
                    return pixelChannel.read(dst);
                }
                var read = pixelStream.read(pixels);
                if (read == -1) {
                    throw new IOException("secret too large");
                } else if (read < getBufferSize()) {
                    dst.put(pixels, 0, read);
                    return read;
                }
                dst.put(transformByte(pixels, (byte) secretByte));
                return getBufferSize();
            }

            @Override
            public boolean isOpen() {
                return pixelChannel.isOpen();
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

}
