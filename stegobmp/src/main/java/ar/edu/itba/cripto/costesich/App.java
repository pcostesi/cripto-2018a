package ar.edu.itba.cripto.costesich;

import ar.edu.itba.cripto.costesich.cli.CliOptions;
import ar.edu.itba.cripto.costesich.decoder.*;
import ar.edu.itba.cripto.costesich.encoder.*;

import java.io.IOException;

public class App {
    private static CliOptions parseOptions(final String... args) {
        CliOptions values = new CliOptions(args);
        return values;
    }


    public static void embed(CliOptions options) throws IOException {
        var encoder = getEncoder(options);
        var combiner = getCombiner(options);
        var secret = options.getSecretFile();
        var output = options.getOutputBitmap();
        var carrier = options.getCarrierFile();

        encoder.encode(carrier, secret, output, combiner);
    }


    public static void extract(CliOptions options) throws IOException {
        var decoder = getDecoder(options);
        var splitter = getSplitter(options);
        var output = options.getOutputBitmap();
        var carrier = options.getCarrierFile();

        decoder.decode(carrier, output, splitter);
    }

    private static Decoder getDecoder(CliOptions options) {
        if (options.getPassword() != null) {
            var algo = options.getAlgoMode();
            var mode = options.getBlockMode();
            var pass = options.getPassword();
            return new BMPPasswordProtectedDecoder(algo, mode, pass);
        }

        return new BMPRawDecoder();
    }

    private static Splitter getSplitter(CliOptions options) {
        switch (options.getSteganographyMode()) {
            case LSB1:
                return new LSB1Splitter();
            case LSB4:
                return new LSB4Splitter();
            case LSBE:
                return new LSBESplitter();
            default:
                throw new IllegalArgumentException("Illegal LSB mode");
        }
    }

    private static Combiner getCombiner(CliOptions options) {
        switch (options.getSteganographyMode()) {
            case LSB1:
                return new LSB1Combiner();
            case LSB4:
                return new LSB4Combiner();
            case LSBE:
                return new LSBECombiner();
            default:
                throw new IllegalArgumentException("Illegal LSB mode");
        }
    }

    private static Encoder getEncoder(CliOptions options) {
        if (options.getPassword() != null) {
            var algo = options.getAlgoMode();
            var mode = options.getBlockMode();
            var pass = options.getPassword();
            return new BMPPasswordProtectedEncoder(algo, mode, pass);
        }

        return new BMPRawEncoder();
    }


    public static void main(String... args) {
        CliOptions options = parseOptions(args);
        System.out.println("Hello World!");

        try {
            if (options.isHelp()) {
                return;
            } else if (options.isEmbed()) {
                embed(options);
                return;
            } else if (options.isExtract()) {
                extract(options);
                return;
            }
            throw new IllegalArgumentException("This shouldn't happen. Ever.");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
