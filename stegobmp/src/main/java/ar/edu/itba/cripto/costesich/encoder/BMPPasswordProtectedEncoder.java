package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.CipherHelper;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;

import javax.crypto.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

public class BMPPasswordProtectedEncoder<T extends Combiner> extends BMPRawEncoder<T> {

    final CipherHelper cipherHelper;

    public BMPPasswordProtectedEncoder(AlgoMode algo, BlockMode mode, String password, String initVector) {
        this.cipherHelper = new CipherHelper(algo, mode, password, initVector);
    }

    protected InputStream packSecretBytes(File secret) throws IOException {
        var originalBytes = super.packSecretBytes(secret);
        var cipher = cipherHelper.getEncryptionCipher();
        var cipherInputStream = new CipherInputStream(originalBytes, cipher);

        var sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(getCipheredSize(secret, cipher));

        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        var streams = Arrays.asList(
                new ByteArrayInputStream(sizeBuffer.array()),
                cipherInputStream);
        return new SequenceInputStream(Collections.enumeration(streams));
    }

    private int getCipheredSize(File secret, Cipher cipher) {
        var secretFileLength = (int) secret.length();
        var rawLength = 4 + secretFileLength + getExtension(secret).length();
        var blockSize = cipher.getBlockSize();
        return rawLength % blockSize == 0 ? rawLength : (rawLength / blockSize) * (blockSize + 1);
    }

}
