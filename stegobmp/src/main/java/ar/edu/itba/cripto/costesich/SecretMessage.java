package ar.edu.itba.cripto.costesich;

import java.io.InputStream;

public class SecretMessage {
    private final int size;
    private final InputStream stream;
    private final String extension;

    public SecretMessage(int size, InputStream stream, String extension) {
        this.size = size;
        this.stream = stream;
        this.extension = extension;
    }

    public int getSize() {
        return size;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getExtension() {
        return extension;
    }
}
