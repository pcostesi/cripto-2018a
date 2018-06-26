package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.CipherHelper;
import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;

import javax.crypto.CipherInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BMPPasswordProtectedDecoder<T extends Splitter> extends BMPRawDecoder<T> {

    final CipherHelper cipherHelper;

    public BMPPasswordProtectedDecoder(AlgoMode algo, BlockMode mode, String password, String initVector) {
        this.cipherHelper = new CipherHelper(algo, mode, password, initVector);
    }

    public BMPPasswordProtectedDecoder(AlgoMode algo, BlockMode mode, String password) {
        this.cipherHelper = new CipherHelper(algo, mode, password, CipherHelper.DEFAULT_IV);
    }

    @Override
    protected SecretMessage recompose(ReadableByteChannel encodedChannel) throws IOException {
        var secretLength = readSecretLength(encodedChannel);
        var cipher = cipherHelper.getDecryptionCipher();
        System.out.println(secretLength);
        var cipheredInputStream = new BoundedInputStream(Channels.newInputStream(encodedChannel), secretLength);
        var cipherInputStream = new CipherInputStream(cipheredInputStream, cipher);
        return super.recompose(Channels.newChannel(cipherInputStream));
    }


    private int readSecretLength(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        do {
            channel.read(buffer);
        } while (buffer.position() < Integer.SIZE / Byte.SIZE);
        buffer.flip();
        return buffer.getInt();
    }
}
