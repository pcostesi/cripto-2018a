package ar.edu.itba.cripto.costesich.bmp;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class BMPPixel {
    private static final int PIXEL_SIZE = 3 * Integer.SIZE / 8;
    private final int red;
    private final int green;
    private final int blue;

    public BMPPixel(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public static BMPPixel read(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(PIXEL_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int read = channel.read(buffer);
        buffer.rewind();
        if (read < PIXEL_SIZE) {
            return null;
        }
        var red = buffer.getInt();
        var green = buffer.getInt();
        var blue = buffer.getInt();

        return new BMPPixel(red, green, blue);
    }

    public BMPPixel write(WritableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(PIXEL_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(red);
        buffer.putInt(green);
        buffer.putInt(blue);
        buffer.flip();
        channel.write(buffer);

        return this;
    }

}
