package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BMPRawDecoder<T extends Splitter> extends BMPDecoder<T> {
    private final static Logger logger = LoggerFactory.getLogger(BMPRawDecoder.class);

    @Override
    protected SecretMessage recompose(ReadableByteChannel encodedChannel, BMPHeader header) throws IOException {
        var fileLength = readFileLength(encodedChannel);
        if (fileLength < 0 || fileLength > header.getImageSize()) {
            throw new IOException("Embedded size doesn't make sense: " + fileLength);
        }
        return new SecretMessage(fileLength, Channels.newInputStream(encodedChannel), null);
    }


    private int readFileLength(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        do {
            if (channel.read(buffer) == -1) {
                throw new IOException("Unexpected end of BMP file reading file length");
            }
        } while (buffer.position() < buffer.capacity());
        buffer.flip();
        return buffer.getInt();
    }


    @Override
    public String toString() {
        return "raw decoder";
    }
}
