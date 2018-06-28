package ar.edu.itba.cripto.costesich.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CliOptions {

    private boolean errorFree;

    @Option(name = "-g", aliases = {"--guess"}, required = false,
            usage = "Brute force raw embedded files", forbids = {"-embed", "--embed", "-extract", "--extract"})
    private boolean guess;

    @Option(name = "-q", aliases = {"--quiet"}, required = false,
            usage = "Shhhh")
    private boolean quiet;

    @Option(name = "-h", aliases = {"--help"}, required = false,
            usage = "Help", forbids = {"-embed", "--embed", "-extract", "--extract"})
    private boolean help;

    @Option(name = "-embed", aliases = {"--embed"}, depends = {"-in"},
            usage = "Embed mode", forbids = {"-extract", "--extract"})
    private boolean embed;

    @Option(name = "-extract", aliases = {"--extract"}, required = false,
            usage = "Extract mode", forbids = {"-embed", "--embed"})
    private boolean extract;

    @Option(name = "-in", aliases = {"--input"}, required = false,
            usage = "File to hide️")
    private File secretFile;

    @Option(name = "-p", aliases = {"--portador"}, required = true,
            usage = "Carrier file️️")
    private File carrierFile;

    @Option(name = "-out", aliases = {"--out"}, required = true,
            usage = "Output file")
    private File outputBitmap;

    @Option(name = "-steg", aliases = {"--steg"}, required = true,
            usage = "Steganography mode.")
    private SteganograpyMode steganographyMode;

    @Option(name = "-a", aliases = {"--algorithm"}, required = false,
            usage = "Algorithm used.")
    private AlgoMode algoMode = AlgoMode.aes128;

    @Option(name = "-m", aliases = {"--mode"}, required = false,
            usage = "Mode")
    private BlockMode blockMode = BlockMode.cbc;

    @Option(name = "-pass", aliases = {"--pass"}, required = false,
            usage = "Password. Do I need to explain it?")
    private String password;


    public CliOptions(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            errorFree = true;

            parser.parseArgument(args);
            if (isHelp()) {
                parser.printUsage(System.out);
                System.exit(0);
            }
            if (isEmbed() && getSecretFile() == null) {
                errorFree = false;
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.out);
            System.exit(1);
        }
    }

    public boolean isEmbed() {
        return embed;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

    public boolean isExtract() {
        return extract;
    }

    public void setExtract(boolean extract) {
        this.extract = extract;
    }

    public File getSecretFile() {
        return secretFile;
    }

    public void setSecretFile(File secretFile) {
        this.secretFile = secretFile;
    }

    public File getCarrierFile() {
        return carrierFile;
    }

    public void setCarrierFile(File carrierFile) {
        this.carrierFile = carrierFile;
    }

    public File getOutputBitmap() {
        return outputBitmap;
    }

    public void setOutputBitmap(File outputBitmap) {
        this.outputBitmap = outputBitmap;
    }

    public SteganograpyMode getSteganographyMode() {
        return steganographyMode;
    }

    public void setSteganographyMode(SteganograpyMode steganographyMode) {
        this.steganographyMode = steganographyMode;
    }

    public AlgoMode getAlgoMode() {
        return algoMode;
    }

    public void setAlgoMode(AlgoMode algoMode) {
        this.algoMode = algoMode;
    }

    public BlockMode getBlockMode() {
        return blockMode;
    }

    public void setBlockMode(BlockMode blockMode) {
        this.blockMode = blockMode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isGuess() {
        return guess;
    }

    public void setGuess(boolean guess) {
        this.guess = guess;
    }
}