package ar.edu.itba.cripto.costesich.decoder;

import java.io.File;

public abstract class BMPDecoder<T extends Splitter> implements Decoder<T> {
    @Override
    public void decode(File image, File output, T splitter) {
    }
}
