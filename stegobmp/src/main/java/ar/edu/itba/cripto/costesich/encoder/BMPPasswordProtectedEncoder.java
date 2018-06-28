package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.utils.CipherHelper;
import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

public class BMPPasswordProtectedEncoder<T extends Combiner> extends BMPRawEncoder<T> {
    private static final Logger logger = LoggerFactory.getLogger(BMPPasswordProtectedEncoder.class);

    final CipherHelper cipherHelper;

    public BMPPasswordProtectedEncoder(AlgoMode algo, BlockMode mode, String password) {
        this.cipherHelper = new CipherHelper(algo, mode, password);
    }

    protected SecretMessage packSecretBytes(File secret) throws IOException {
        var originalBytes = super.packSecretBytes(secret);
        var cipher = cipherHelper.getEncryptionCipher();

        // workaround: we need an inputstream, but the cipher will encode the data
        // to an outputstream. So we just pipe data from one to the other.
        var tempOutputStream = new ByteArrayOutputStream();
        var cipherOutputStream = new CipherOutputStream(tempOutputStream, cipher);

        // Then we encrypt the data using the original message as a source
        originalBytes.getStream().transferTo(cipherOutputStream);
        var size = getCipheredSize(originalBytes, cipher);
        cipherOutputStream.close();
        tempOutputStream.close();

        var extension = originalBytes.getExtension();

        var sizeBuffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        sizeBuffer.order(ByteOrder.BIG_ENDIAN);
        sizeBuffer.putInt(size);
        sizeBuffer.flip();

        logger.info("Original message length: {}", originalBytes.getSize());
        logger.info("Encrypted message length: {}", size);


        var streams = Arrays.asList(
                new ByteArrayInputStream(sizeBuffer.array()),
                new ByteArrayInputStream(tempOutputStream.toByteArray()));
        return new SecretMessage(size, new SequenceInputStream(Collections.enumeration(streams)), extension);
    }

    private int getCipheredSize(SecretMessage secret, Cipher cipher) {
        return cipher.getOutputSize(secret.getSize() + Integer.SIZE / Byte.SIZE + secret.getExtension().length());
    }

}
