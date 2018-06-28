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
        var fileContent = readFileContent(encodedChannel, fileLength);
        var extension = readFileExtension(encodedChannel);
        logger.info("file length is {}, file extension is {}", fileLength, extension);
        return new SecretMessage(fileLength, fileContent, extension);
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

    private InputStream readFileContent(ReadableByteChannel channel, int fileLength) throws IOException {
        var buffer = ByteBuffer.allocate(fileLength);
        do {
            if (channel.read(buffer) == -1) {
                throw new IOException("Unexpected end of BMP file reading body");
            }
        } while (buffer.position() < buffer.capacity());
        buffer.rewind();
        return new ByteArrayInputStream(buffer.array());
    }

    private String readFileExtension(ReadableByteChannel channel) throws IOException {
        var is = Channels.newInputStream(channel);
        int c;
        var builder = new StringBuilder();
        while ((c = is.read()) > 0) {
            builder.append((char) c);
        }
        if (c == -1) {
            throw new IOException("Unexpected end of BMP file reading extension");
        }
        return builder.toString();
    }
}
