package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class LSBECombiner implements Combiner {

    @Override
    public ReadableByteChannel combineAll(BMPHeader header, ReadableByteChannel pixelChannel, SecretMessage secret) throws IOException {
        return null;
    }
}
