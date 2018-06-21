package ar.edu.itba.cripto.costesich;

import ar.edu.itba.cripto.costesich.cli.CliOptions;

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
        CliOptions options = parseOptions(args);
        System.out.println( "Hello World!" );
    }
}
