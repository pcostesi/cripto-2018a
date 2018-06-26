package ar.edu.itba.cripto.costesich.decoder;

import java.io.File;
import java.io.IOException;

public interface Decoder<S extends Splitter> {
    void decode(File image, File output, S splitter) throws IOException;
}
