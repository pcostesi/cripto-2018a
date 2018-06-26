package ar.edu.itba.cripto.costesich.bmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class BMPDIBHeader {
    private static final Logger logger = LoggerFactory.getLogger(BMPDIBHeader.class);
    private final static int HEADER_SIZE = 36;
    private final int width;
    private final int height;
    private final short colorPlanes;
    private final short bitsPerPixel;
    private final int compressionMode;
    private final int imageSize;
    private final int horizontalResolution;
    private final int verticalResolution;
    private final int numberOfColors;
    private final int numberOfImportantColors;

    public BMPDIBHeader(int width, int height, short colorPlanes, short bitsPerPixel, int compressionMode, int imageSize, int horizontalResolution, int verticalResolution, int numberOfColors, int numberOfImportantColors) {
        this.width = width;
        this.height = height;
        this.colorPlanes = colorPlanes;
        this.bitsPerPixel = bitsPerPixel;
        this.compressionMode = compressionMode;
        this.imageSize = imageSize;
        this.horizontalResolution = horizontalResolution;
        this.verticalResolution = verticalResolution;
        this.numberOfColors = numberOfColors;
        this.numberOfImportantColors = numberOfImportantColors;
        logger.info("Parsed DIB header with the following data:\n{}", this.toString());
    }

    public static BMPDIBHeader read(ReadableByteChannel channel) throws IOException {
        var sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);

        channel.read(sizeBuffer);
        sizeBuffer.rewind();
        int size = sizeBuffer.getInt();
        switch (size) {
            case 40:
                return channelToBitmapInfoHeader(channel);
            default:
                throw new IllegalArgumentException("Unsupported DIB header, got size: " + size);
        }
    }

    private static BMPDIBHeader channelToBitmapInfoHeader(ReadableByteChannel input) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        var read = input.read(buffer);
        buffer.rewind();
        if (read < HEADER_SIZE) {
            throw new IllegalArgumentException("couldn't read enough bytes");
        }
        var width = buffer.getInt();
        var height = buffer.getInt();
        var colorPlanes = buffer.getShort();
        var bitsPerPixel = buffer.getShort();
        var compressionMode = buffer.getInt();
        var imageSize = buffer.getInt();
        var horizontalResolution = buffer.getInt();
        var verticalResolution = buffer.getInt();
        var numberOfColors = buffer.getInt();
        var numberOfImportantColors = buffer.getInt();
        return new BMPDIBHeader(width, height, colorPlanes, bitsPerPixel, compressionMode, imageSize, horizontalResolution, verticalResolution, numberOfColors, numberOfImportantColors);
    }

    public BMPDIBHeader write(WritableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(HEADER_SIZE + 4);
        buffer.putInt(width);
        buffer.putInt(height);
        buffer.putShort(colorPlanes);
        buffer.putShort(bitsPerPixel);
        buffer.putInt(compressionMode);
        buffer.putInt(imageSize);
        buffer.putInt(horizontalResolution);
        buffer.putInt(verticalResolution);
        buffer.putInt(numberOfColors);
        buffer.putInt(numberOfImportantColors);

        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public short getColorPlanes() {
        return colorPlanes;
    }

    public short getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getCompressionMode() {
        return compressionMode;
    }

    public int getImageSize() {
        return imageSize;
    }

    public int getHorizontalResolution() {
        return horizontalResolution;
    }

    public int getVerticalResolution() {
        return verticalResolution;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

    public int getNumberOfImportantColors() {
        return numberOfImportantColors;
    }


    @Override
    public String toString() {
        return String.format(
                "width: %d\n" +
                        "height: %d\n" +
                        "color planes: %d\n" +
                        "bits per pixel: %d\n" +
                        "compression mode: %d\n" +
                        "image size: %d\n" +
                        "horiz. res: %d\n" +
                        "vert. res: %d\n" +
                        "no. of colors: %d\n" +
                        "no. of important colors: %d",
                width, height, colorPlanes, bitsPerPixel, compressionMode,
                imageSize, horizontalResolution, verticalResolution, numberOfColors,
                numberOfImportantColors);
    }
}
