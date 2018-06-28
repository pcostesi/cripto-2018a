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

public class LSBECombiner implements Combiner {
    private static final Logger logger = LoggerFactory.getLogger(LSBCombiner.class);

    @Override
    public ReadableByteChannel combineAll(BMPHeader header, ReadableByteChannel pixelChannel, SecretMessage secret) throws IOException {
        var pixels = Channels.newInputStream(pixelChannel);
        return getByteStream(pixels, secret.getStream());
    }

    private ReadableByteChannel getByteStream(InputStream pixels, InputStream secret) {
        return new ReadableByteChannel() {
            private int idx = 0;
            private int secretByte;
            private boolean done;
            private int encodeable = 0;

            @Override
            public int read(ByteBuffer dst) throws IOException {
                var read = 0;

                // fast copy
                if (done) {
                    var buffer = new byte[8192]; // a reasonable buffer size
                    var toRead = Math.min(dst.remaining(), buffer.length);
                    read = pixels.read(buffer, 0, toRead);
                    if (read == -1) {
                        pixels.close();
                        return -1;
                    }
                    dst.put(buffer, 0, read);
                    return read;
                }

                // bit interpolation
                while (dst.hasRemaining()) {
                    var pixel = pixels.read();
                    if (pixel == -1) {
                        if (!done) {
                            var max = encodeable / Byte.SIZE;
                            throw new IOException("Carrier image too small. Can fit at most " + max + " bytes.");
                        }
                        // EOF, but we could embed the payload completely.
                        return read;
                    }

                    // we need to interpolate a new byte
                    if (idx <= 0) {
                        secretByte = secret.read();
                        idx = Byte.SIZE;
                    }
                    // but the secret is EOF! So we just finish copying and default to fast-copy.
                    if (secretByte == -1) {
                        done = true;
                        secret.close();
                    }

                    // interpolate byte
                    if (pixel == 255 || pixel == 254) {
                        var value = (pixel & 0b11111110) | ((secretByte >> --idx) & 0b00000001);
                        encodeable++;
                        dst.put((byte) value);
                    } else {
                        dst.put((byte) pixel);
                    }

                    read++;
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
