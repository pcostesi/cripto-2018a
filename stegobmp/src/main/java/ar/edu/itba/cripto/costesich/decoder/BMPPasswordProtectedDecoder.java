package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.bmp.BMPHeader;
import ar.edu.itba.cripto.costesich.utils.CipherHelper;
import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BMPPasswordProtectedDecoder<T extends Splitter> extends BMPRawDecoder<T> {
    private static final Logger logger = LoggerFactory.getLogger(BMPPasswordProtectedDecoder.class);
    private final AlgoMode algo;
    private final BlockMode mode;
    private final String password;

    public BMPPasswordProtectedDecoder(AlgoMode algo, BlockMode mode, String password) {
        this.algo = algo;
        this.mode = mode;
        this.password = password;
    }

    @Override
    protected SecretMessage recompose(ReadableByteChannel encodedChannel, BMPHeader header) throws IOException {
        var secretLength = readSecretLength(encodedChannel);
        logger.info("Encrypted length is {}", secretLength);
        if (secretLength < 0 || secretLength > header.getImageSize()) {
            throw new IOException("Encrypted lenght doesn't make sense: " + secretLength);
        }
        var cipherHelper = new CipherHelper(algo, mode, password);
        var cipher = cipherHelper.getDecryptionCipher();
        if (secretLength % cipher.getBlockSize() != 0) {
            throw new IOException("The embedded file wasn't padded correctly");
        }
        var cipheredInputStream = readFileContent(encodedChannel, secretLength, cipher);

        var decryptedInputStream = new CipherInputStream(cipheredInputStream, cipher);
        return super.recompose(Channels.newChannel(decryptedInputStream), header);
    }


    private InputStream readFileContent(ReadableByteChannel channel, int fileLength, Cipher c) throws IOException {
        fileLength = c.getOutputSize(fileLength);
        System.out.println(fileLength);
        var buffer = ByteBuffer.allocate(fileLength);
        do {
            channel.read(buffer);
        } while (buffer.position() < buffer.capacity());
        return new ByteArrayInputStream(buffer.array());
    }


    private int readSecretLength(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        do {
            channel.read(buffer);
        } while (buffer.position() < buffer.capacity());
        buffer.flip();
        return buffer.getInt();
    }
}
