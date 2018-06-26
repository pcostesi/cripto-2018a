package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.bmp.BMPHeader;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

public interface Splitter {
    ReadableByteChannel splitAll(BMPHeader header, ReadableByteChannel pixelChannel) throws IOException;
}
