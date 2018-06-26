package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.CipherHelper;
import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
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

    public BMPPasswordProtectedEncoder(AlgoMode algo, BlockMode mode, String password) {
        this.cipherHelper = new CipherHelper(algo, mode, password, CipherHelper.DEFAULT_IV);
    }

    protected SecretMessage packSecretBytes(File secret) throws IOException {
        var originalBytes = super.packSecretBytes(secret);
        var cipher = cipherHelper.getEncryptionCipher();


        var tempOutputStream = new ByteArrayOutputStream();
        // workaround: we need an inputstream, but the cipher will encode the data
        // to an outputstream. So we just pipe data from one to the other.
        // and we encrypt the data, using the original message as a source
        var cipherOutputStream = new CipherOutputStream(tempOutputStream, cipher);
        originalBytes.getStream().transferTo(cipherOutputStream);
        var cipherInputStream = new ByteArrayInputStream(tempOutputStream.toByteArray());

        var size = getCipheredSize(originalBytes, cipher);
        var extension = originalBytes.getExtension();

        var sizeBuffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        sizeBuffer.putInt(size);
        sizeBuffer.flip();

        var streams = Arrays.asList(
                new ByteArrayInputStream(sizeBuffer.array()),
                cipherInputStream);
        return new SecretMessage(size, new SequenceInputStream(Collections.enumeration(streams)), extension);
    }

    private int getCipheredSize(SecretMessage secret, Cipher cipher) {
        var secretFileLength = secret.getSize();
        var rawLength = 4 + secretFileLength + secret.getExtension().length;
        var blockSize = cipher.getBlockSize();
        System.out.println("raw = " + rawLength);
        return rawLength % blockSize == 0 ? rawLength : (Math.floorDiv(rawLength, blockSize) + 1) * blockSize;
    }

}
