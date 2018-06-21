package ar.edu.itba.cripto.costesich.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.nio.file.Path;

public class CliOptions {

    private boolean errorFree;

    @Option(name = "-embed", aliases = { "--embed"}, required = false,
            usage = "Embed mode", forbids={"-extract", "--extract"})
    private boolean embed;

    @Option(name = "-extract", aliases = { "--extract"}, required = false,
            usage = "Extract mode", forbids={"-embed", "--embed"})
    private boolean extract;

    @Option(name = "-in", aliases = { "--input"}, required = false,
            usage = "File to hide 🕵🏻‍♂️")
    private Path inputBitmap;

    @Option(name = "-p", aliases = { "--portador"}, required = true,
            usage = "Carrier file 🧟‍♂️️")
    private Path carrierFile;

    @Option(name = "-out", aliases = { "--out"}, required = true,
            usage = "Output file")
    private Path outputBitmap;

    @Option(name = "-steg", aliases = { "--steg"}, required = true,
            usage = "Steganography mode. May choose between LSB1, LSB4, LSBE")
    private SteganograpyMode steganographyMode;

    @Option(name = "-a", aliases = { "--algorithm"}, required = false,
            usage = "Algorithm used. <aes128 | aes192 | aes256 | des>")
    private AlgoMode algoMode;

    @Option(name = "-m", aliases = { "--mode"}, required = false,
            usage = " <ecb | cfb | ofb | cbc>")
    private BlockMode blockMode;

    @Option(name = "-pass", aliases = { "--pass"}, required = false,
            usage = "Password. Do I need to explain it?")
    private String password;


    public CliOptions(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            errorFree = true;

            parser.parseArgument(args);
            if (isEmbed() && getInputBitmap() == null) {
                errorFree = false;
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
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

    public Path getInputBitmap() {
        return inputBitmap;
    }

    public void setInputBitmap(Path inputBitmap) {
        this.inputBitmap = inputBitmap;
    }

    public Path getCarrierFile() {
        return carrierFile;
    }

    public void setCarrierFile(Path carrierFile) {
        this.carrierFile = carrierFile;
    }

    public Path getOutputBitmap() {
        return outputBitmap;
    }

    public void setOutputBitmap(Path outputBitmap) {
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
}