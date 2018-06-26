package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.encoder.BMPRawEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        do {
            channel.read(buffer);
        } while (buffer.position() < Integer.SIZE / Byte.SIZE);
        buffer.flip();
        return buffer.getInt();
    }

    private InputStream readFileContent(ReadableByteChannel channel, int fileLength) {
        return new BoundedInputStream(Channels.newInputStream(channel), fileLength);
    }

    private byte[] readFileExtension(ReadableByteChannel channel) {
        return new byte[]{};
    }
}
