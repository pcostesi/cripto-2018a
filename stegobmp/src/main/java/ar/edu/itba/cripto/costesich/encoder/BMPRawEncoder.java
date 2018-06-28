package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BMPRawEncoder<T extends Combiner> extends BMPEncoder<T> {
    private static final Logger logger = LoggerFactory.getLogger(BMPRawEncoder.class);

    protected SecretMessage packSecretBytes(File secret) throws IOException {
        var length = (int) secret.length();
        var sizeBuffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        var extension = getExtension(secret);

        logger.info("Creating payload. Raw size: {}, extension: {}", length, extension);

        sizeBuffer.order(ByteOrder.BIG_ENDIAN);
        sizeBuffer.putInt(length);
        sizeBuffer.flip();

        List<InputStream> streams = Arrays.asList(
                new ByteArrayInputStream(sizeBuffer.array()),
                Files.newInputStream(secret.toPath()),
                new ByteArrayInputStream(extension.getBytes()),
                new ByteArrayInputStream(new byte[]{ 0 }));

        return new SecretMessage(length, new SequenceInputStream(Collections.enumeration(streams)), extension);
    }

    protected final String getExtension(File file) {
        var filename = file.toPath().getFileName().toString();
        return filename.substring(filename.lastIndexOf('.'), filename.length());
    }
}
