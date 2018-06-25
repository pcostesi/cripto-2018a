package ar.edu.itba.cripto.costesich;

import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;
import ar.edu.itba.cripto.costesich.cli.CliOptions;
import ar.edu.itba.cripto.costesich.encoder.BMPPasswordProtectedEncoder;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App {
    private static CliOptions parseOptions(final String... args) {
        CliOptions values = new CliOptions(args);
        return values;
    }


    public static void main(String... args) {
        //CliOptions options = parseOptions(args);
        System.out.println( "Hello World!" );
        var encoder = new BMPPasswordProtectedEncoder(AlgoMode.aes256, BlockMode.ecb, "Password", "something");
        try {
            encoder.encode(Paths.get("image.bmp").toFile(),
                    Paths.get("secret.txt").toFile(),
                    Paths.get("output.bmp").toFile(),
                    null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
