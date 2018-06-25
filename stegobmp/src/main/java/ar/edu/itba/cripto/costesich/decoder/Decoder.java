package ar.edu.itba.cripto.costesich.decoder;

import java.io.*;

public interface Decoder <S extends Splitter> {
    void decode(File image, File output, S splitter);
}
