package ar.edu.itba.cripto.costesich.encoder;

import java.io.*;

public interface Encoder<C extends Combiner> {
    void encode(File image, File secret, File output, C combiner) throws IOException;
}
