package ar.edu.itba.cripto.costesich;

import ar.edu.itba.cripto.costesich.cli.CliOptions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Hello world!
 *
 */
public class App {
    private static CliOptions parseOptions(final String... args) {
        CliOptions values = new CliOptions(args);
        CmdLineParser parser = new CmdLineParser(values);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Couldn't parse the command line");
            System.exit(1);
        }
        return values;
    }


    public static void main(String... args) {
        CliOptions options = parseOptions(args);
        System.out.println( "Hello World!" );
    }
}
