package ar.edu.itba.cripto.costesich.decoder;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {
    private final InputStream original;
    private final int maxSize;
    private int read;

    public BoundedInputStream(InputStream original, int maxSize) {
        this.original = original;
        this.maxSize = maxSize;
    }

    @Override
    public int read() throws IOException {
        if (read++ >= maxSize) {
            return -1;
        }
        return original.read();
    }

    public InputStream getOriginal() {
        return original;
    }
}
