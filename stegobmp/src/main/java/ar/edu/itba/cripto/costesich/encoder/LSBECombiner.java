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
import java.util.Arrays;

public class LSBECombiner implements Combiner {
    private static final Logger logger = LoggerFactory.getLogger(LSBCombiner.class);

    @Override
    public ReadableByteChannel combineAll(BMPHeader header, ReadableByteChannel pixelChannel, SecretMessage secret) throws IOException {
        var bytes = Channels.newInputStream(pixelChannel).readAllBytes();
        var encodeable = 0;
        for (var i = 0; i < bytes.length; i++) {
            if (bytes[i] == (byte)255 || bytes[i] == (byte)254 ) {
                encodeable++;
            }
        }
        if (encodeable < secret.getSize()) {
            throw new IllegalArgumentException("Secret file too large. Can fit at most " + encodeable + "bytes");
        }

        pixelChannel.close();
        return getByteStream(bytes, secret.getStream());
    }

    private ReadableByteChannel getByteStream(byte[] pixels, InputStream secret) {

        return new ReadableByteChannel() {
            private int idx = 0;
            private int secretByte;
            private boolean done;
            private int pixelIdx = 0;

            @Override
            public int read(ByteBuffer dst) throws IOException {
                if (pixelIdx >= pixels.length) {
                    return -1;
                }
                var read = 0;
                while (dst.hasRemaining() && pixelIdx < pixels.length) {
                    if (done) {
                        // fast copy from buffer
                        var available = Math.min(pixels.length - pixelIdx, dst.remaining());
                        dst.put(pixels, pixelIdx, available);
                        pixelIdx += available;
                        return available + read;
                    }
                    if (idx <= 0) {
                        secretByte = secret.read();
                        idx = Byte.SIZE;
                    }
                    if (secretByte == -1) {
                        done = true;
                        break;
                    }
                    if (pixels[pixelIdx] == (byte)255 || pixels[pixelIdx] == (byte)254) {
                        var value = (byte) ((pixels[pixelIdx] & 0b11111110) | ((secretByte >> --idx) & 0b00000001));
                        dst.put(value);
                    } else {
                        dst.put(pixels[pixelIdx]);
                    }
                    read++;
                    pixelIdx++;
                }
                return read;
            }

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close() throws IOException {
            }
        };
    }
}
