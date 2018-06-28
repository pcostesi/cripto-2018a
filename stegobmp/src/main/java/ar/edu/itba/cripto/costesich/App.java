package ar.edu.itba.cripto.costesich;

import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;
import ar.edu.itba.cripto.costesich.cli.CliOptions;
import ar.edu.itba.cripto.costesich.decoder.*;
import ar.edu.itba.cripto.costesich.encoder.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

public class App {
    private static CliOptions parseOptions(final String... args) {
        CliOptions values = new CliOptions(args);
        return values;
    }

    public static void guess(CliOptions options) {
        var splitters = new Splitter[] { new LSB1Splitter(), new LSB4Splitter(), new LSBESplitter() };
        var decoder = new BMPRawDecoder();
        var carrier = options.getCarrierFile();

        var rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        for (var splitter : splitters) {
            try {
                System.out.println("Guessing with " + splitter.getClass().getSimpleName());
                var ext = decoder.decode(carrier, Files.createTempFile("stegobmp-", "-guess").toFile(), splitter);
                if (!ext.startsWith(".")) {
                    System.out.println("- Extension does not seem to be valid");
                    continue;
                }
                System.out.println("- " + carrier.getName() + " seems to be a " + ext +
                        " encoded with " + splitter.getClass().getSimpleName().substring(0, 4));
                decoder.decode(carrier, options.getOutputBitmap(), splitter);
            } catch (Exception e) {
                System.out.println("- Hmm, nope.");
            }
        }

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

        if (options.isQuiet()) {
            var rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.WARN);
        }

        try {
            if (options.isHelp()) {
                return;
            } else if (options.isGuess()) {
                guess(options);
                return;
            } if (options.isEmbed()) {
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
