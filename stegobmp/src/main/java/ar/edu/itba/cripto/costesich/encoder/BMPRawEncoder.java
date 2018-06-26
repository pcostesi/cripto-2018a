package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import org.bouncycastle.util.Strings;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BMPRawEncoder<T extends Combiner> extends BMPEncoder<T> {

    protected SecretMessage packSecretBytes(File secret) throws IOException {
        var length = (int) secret.length();
        var sizeBuffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        var extension = Strings.toByteArray(getExtension(secret));

        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        sizeBuffer.putInt(length);
        sizeBuffer.flip();

        List<InputStream> streams = Arrays.asList(
                new ByteArrayInputStream(sizeBuffer.array()),
                Files.newInputStream(secret.toPath()),
                new ByteArrayInputStream(extension));

        return new SecretMessage(length, new SequenceInputStream(Collections.enumeration(streams)), extension);
    }

    protected final String getExtension(File file) {
        var filename = file.toPath().getFileName().toString();
        return filename.substring(filename.lastIndexOf('.'), filename.length()) + '\0';
    }
}
