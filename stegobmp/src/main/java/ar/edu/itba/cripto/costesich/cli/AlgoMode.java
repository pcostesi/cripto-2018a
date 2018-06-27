package ar.edu.itba.cripto.costesich.cli;

public enum AlgoMode {
    aes128(16, "AES", 16),
    aes192(24, "AES", 24),
    aes256(32, "AES", 32),
    des(64, "DES", 8);

    private final int blockSize;
    private final String algoName;
    private final int padSize;

    AlgoMode(int blockSize, String algoName, int padSize) {
        this.blockSize = blockSize;
        this.algoName = algoName;
        this.padSize = padSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public String getAlgoName() {
        return algoName;
    }

    public int getPadSize() {
        return padSize;
    }
}
