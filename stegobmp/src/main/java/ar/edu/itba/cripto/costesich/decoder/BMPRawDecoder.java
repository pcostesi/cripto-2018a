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

public class BMPRawDecoder<T extends Splitter> extends BMPDecoder<T> {
    private final static Logger logger = LoggerFactory.getLogger(BMPRawDecoder.class);

    @Override
    protected SecretMessage recompose(ReadableByteChannel encodedChannel) throws IOException {
        var fileLength = readFileLength(encodedChannel);
        logger.info("file length is {}", fileLength);
        var is = Channels.newInputStream(encodedChannel);
        var bytes = is.readAllBytes();
        System.out.println(bytes.length);
        var fileContent = readFileContent(bytes, fileLength);
        var extension = readFileExtension(bytes, fileLength);

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

    private InputStream readFileContent(byte[] bytes, int fileLength) throws IOException {
        return new ByteArrayInputStream(bytes, 0, fileLength);
    }

    private byte[] readFileExtension(byte[] bytes, int fileLength) throws IOException {
        var end = fileLength;

        while (bytes[end] != 0 && end < bytes.length) {
            end++;
        }
        return Arrays.copyOfRange(bytes, fileLength, end);
    }
}
