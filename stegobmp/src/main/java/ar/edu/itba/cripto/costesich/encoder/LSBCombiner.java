package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    protected abstract void transformByte(byte[] bytes, byte secretByte);

    protected abstract int getBufferSize();

    protected ReadableByteChannel getByteStream(ReadableByteChannel pixelChannel, InputStream secret, int fileSize) {

        var pixelBuffer = ByteBuffer.allocate(getBufferSize());
        pixelBuffer.order(ByteOrder.LITTLE_ENDIAN);

        return new ReadableByteChannel() {
            private boolean isOpen = pixelChannel.isOpen();
            private byte[] bytes = pixelBuffer.array();
            private boolean doneWithSecret = false;

            private int insertByte(int secretByte) throws IOException {
                var read = pixelChannel.read(pixelBuffer);
                pixelBuffer.flip();
                if (secretByte == -1) {
                    doneWithSecret = true;
                    return read;
                }
                if (secretByte != -1 && read == getBufferSize()) {
                    transformByte(bytes, (byte) secretByte);
                }
                return read;
            }


            @Override
            public int read(ByteBuffer dst) throws IOException {
                if (doneWithSecret) {
                    return pixelChannel.read(dst);
                }
                var read = insertByte(secret.read());
                if (read == -1) {
                    return -1;
                }
                dst.put(bytes, 0, read);
                return read;
            }

            @Override
            public boolean isOpen() {
                return pixelChannel.isOpen() && isOpen;
            }

            @Override
            public void close() throws IOException {
                pixelChannel.close();
                secret.close();
            }
        };
    }

}
