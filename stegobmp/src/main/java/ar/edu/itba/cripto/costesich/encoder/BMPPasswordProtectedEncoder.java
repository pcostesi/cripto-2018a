package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.CipherHelper;
import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

public class BMPPasswordProtectedEncoder<T extends Combiner> extends BMPRawEncoder<T> {

    final CipherHelper cipherHelper;

    public BMPPasswordProtectedEncoder(AlgoMode algo, BlockMode mode, String password, String initVector) {
        this.cipherHelper = new CipherHelper(algo, mode, password, initVector);
    }

    public BMPPasswordProtectedEncoder(AlgoMode algo, BlockMode mode, String password) {
        this.cipherHelper = new CipherHelper(algo, mode, password, CipherHelper.DEFAULT_IV);
    }

    protected SecretMessage packSecretBytes(File secret) throws IOException {
        var originalBytes = super.packSecretBytes(secret);
        var cipher = cipherHelper.getEncryptionCipher();
        var cipherInputStream = new CipherInputStream(originalBytes.getStream(), cipher);
        var size = getCipheredSize(originalBytes, cipher);
        var extension = originalBytes.getExtension();

        var sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(size);

        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        var streams = Arrays.asList(
                new ByteArrayInputStream(sizeBuffer.array()),
                cipherInputStream);
        return new SecretMessage(size, new SequenceInputStream(Collections.enumeration(streams)), extension);
    }

    private int getCipheredSize(SecretMessage secret, Cipher cipher) {
        var secretFileLength = secret.getSize();
        var rawLength = 4 + secretFileLength;
        var blockSize = cipher.getBlockSize();
        return rawLength % blockSize == 0 ? rawLength : (rawLength / blockSize) * (blockSize + 1);
    }

}
