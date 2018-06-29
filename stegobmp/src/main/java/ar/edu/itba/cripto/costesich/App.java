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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;

public class App {
    private static CliOptions parseOptions(final String... args) {
        CliOptions values = new CliOptions(args);
        return values;
    }

    public static void guess(CliOptions options) {
        var rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        if (options.getPassword() == null) {
            guessWithoutPassword(options);
        } else {
            guessWithPassword(options);
        }
    }

    private static void guessWithPassword(CliOptions options) {
        var splitters = new Splitter[] { new LSB4Splitter(), new LSB1Splitter(), new LSBESplitter() };
        var carrier = options.getCarrierFile();

        var decoders = Arrays.stream(AlgoMode.values()).flatMap(algo ->
            Arrays.stream(BlockMode.values()).map(blockMode ->
                new BMPPasswordProtectedDecoder(algo, blockMode, options.getPassword())
        )).toArray(i -> new Decoder[i]);

        for (var decoder : decoders) {
            for (var splitter : splitters) {
                try {
                    var splitterName = splitter.getClass().getSimpleName();
                    var filename = carrier.toPath().getFileName();
                    var cipher = decoder.toString();
                    System.out.println("Guessing if " + filename + " can be decoded with " + splitterName + " + " + cipher);
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

    }

    private static void guessWithoutPassword(CliOptions options) {
        var splitters = new Splitter[] { new LSB4Splitter(), new LSB1Splitter(), new LSBESplitter() };
        var decoder = new BMPRawDecoder();
        var carrier = options.getCarrierFile();

        for (var splitter : splitters) {
            try {
                var splitterName = splitter.getClass().getSimpleName();
                var filename = carrier.toPath().getFileName();
                System.out.println("Guessing if " + filename + " can be decoded with " + splitterName);
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


    private static void printASCIIArt(OutputStream output) {
        var writer = new OutputStreamWriter(output);
        try {
            writer.write("STEGOBMP\n" +
                    "                         .       .\n" +
                    "                        / `.   .' \\\n" +
                    "                .---.  <    > <    >  .---.\n" +
                    "                |    \\  \\ - ~ ~ - /  /    |\n" +
                    "                 ~-..-~             ~-..-~\n" +
                    "             \\~~~\\.'                    `./~~~/\n" +
                    "              \\__/                        \\__/\n" +
                    "               /                  .-    .  \\\n" +
                    "        _._ _.-    .-~ ~-.       /       }   \\/~~~/\n" +
                    "    _.-'q  }~     /       }     {        ;    \\__/\n" +
                    "   {'__,  /      (       /      {       /      `. ,~~|   .     .\n" +
                    "    `''''='~~-.__(      /_      |      /- _      `..-'   \\\\   //\n" +
                    "                / \\   =/  ~~--~~{    ./|    ~-.     `-..__\\\\_//_.-'\n" +
                    "               {   \\  +\\         \\  =\\ (        ~ - . _ _ _..---~\n" +
                    "               |  | {   }         \\   \\_\\\n" +
                    "              '---.o___,'       .o___,'\nSTEGO.BMP\n\n\n"
            );
            writer.flush();
        } catch (IOException e) {
        }
    }


    public static void main(String... args) {
        printASCIIArt(System.out);
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
