package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.stream.Stream;

public class BMPRawDecoder<T extends Splitter> extends BMPDecoder<T> {
    private final static Logger logger = LoggerFactory.getLogger(BMPRawDecoder.class);

    @Override
    protected SecretMessage recompose(ReadableByteChannel encodedChannel) throws IOException {
        var fileLength = readFileLength(encodedChannel);
        logger.info("file length is {}", fileLength);
        var fileContent = readFileContent(encodedChannel, fileLength);
        var extension = readFileExtension(encodedChannel);
        return new SecretMessage(fileLength, fileContent, extension);
    }


    private int readFileLength(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        do {
            channel.read(buffer);
        } while (buffer.position() < buffer.capacity());
        buffer.flip();
        return buffer.getInt();
    }

    private InputStream readFileContent(ReadableByteChannel channel, int fileLength) throws IOException {
        var buffer = ByteBuffer.allocate(fileLength);
        do {
            channel.read(buffer);
        } while (buffer.position() < buffer.capacity());
        return new ByteArrayInputStream(buffer.array());
    }

    private String readFileExtension(ReadableByteChannel channel) throws IOException {
        var is = Channels.newInputStream(channel);
        int c;
        var builder = new StringBuilder();
        while ((c = is.read()) > 0) {
            builder.append(c);
        }
        return builder.toString();
    }
}
