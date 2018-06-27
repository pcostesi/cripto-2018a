package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.utils.CipherHelper;
import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected SecretMessage recompose(ReadableByteChannel encodedChannel) throws IOException {
        var secretLength = readSecretLength(encodedChannel);
        logger.info("Encrypted length is {}", secretLength);
        var cipherHelper = new CipherHelper(algo, mode, password);
        var cipher = cipherHelper.getDecryptionCipher();
        // var cipheredInputStream = Channels.newInputStream(encodedChannel);
        var cipheredInputStream = readFileContent(encodedChannel, secretLength);
        var cipherInputStream = new CipherInputStream(cipheredInputStream, cipher);
        return super.recompose(Channels.newChannel(cipherInputStream));
    }

    private InputStream readFileContent(ReadableByteChannel channel, int fileLength) throws IOException {
        var buffer = ByteBuffer.allocate(fileLength);
        channel.read(buffer);
        buffer.rewind();
        return new ByteArrayInputStream(buffer.array());
    }

    private int readSecretLength(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);
        if (channel.read(buffer) == -1) {
            throw new IllegalArgumentException("Malformed file. Could not read encrypted length");
        }
        buffer.flip();
        return buffer.getInt();
    }
}
