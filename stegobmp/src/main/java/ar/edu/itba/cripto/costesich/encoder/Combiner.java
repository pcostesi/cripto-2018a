package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

public interface Combiner {
    ReadableByteChannel combineAll(BMPHeader header, ReadableByteChannel pixelChannel, SecretMessage secret) throws IOException;
}
