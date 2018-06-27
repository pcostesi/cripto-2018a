package ar.edu.itba.cripto.costesich.utils;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class EncrypStream extends InputStream {
    private final Cipher cipher;
    private final InputStream source;
    private final ByteBuffer inputBuffer;
    private final ByteBuffer outputBuffer;


    public EncrypStream(Cipher cipher, InputStream source) {
        this.cipher = cipher;
        this.source = source;
        this.inputBuffer = ByteBuffer.allocateDirect(cipher.getBlockSize());
        this.outputBuffer = ByteBuffer.allocateDirect(cipher.getBlockSize());
    }

    private int updateBuffers() {
        try {
            cipher.update(inputBuffer, outputBuffer);
        } catch (ShortBufferException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
