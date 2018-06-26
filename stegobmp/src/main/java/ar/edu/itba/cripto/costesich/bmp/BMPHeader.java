package ar.edu.itba.cripto.costesich.bmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class BMPHeader {
    private static final Logger logger = LoggerFactory.getLogger(BMPDIBHeader.class);

    private static final int HEADER_SIZE = 14;
    private final byte magicNumber1;
    private final byte magicNumber2;
    private final int size;
    private final short reserved1;
    private final short reserved2;
    private final int offset;
    private final int imageSize;
    private final BMPDIBHeader dib;


    private BMPHeader(byte magicNumber1, byte magicNumber2, int size, short reserved1, short reserved2, int offset, BMPDIBHeader dib) {
        this.magicNumber1 = magicNumber1;
        this.magicNumber2 = magicNumber2;
        this.size = size;
        this.reserved1 = reserved1;
        this.reserved2 = reserved2;
        this.offset = offset;
        this.dib = dib;
        this.imageSize = Math.max(this.getDib().getImageSize(), this.getSize() - this.getOffset());

        logger.info("Created base header with the following data:\n{}", toString());
    }

    public static BMPHeader read(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        channel.read(buffer);
        buffer.rewind();
        var magicNumber1 = buffer.get();
        var magicNumber2 = buffer.get();
        var size = buffer.getInt();
        var reserved1 = buffer.getShort();
        var reserved2 = buffer.getShort();
        var offset = buffer.getInt();

        var dib = BMPDIBHeader.read(channel);
        return new BMPHeader(magicNumber1, magicNumber2, size, reserved1, reserved2, offset, dib);
    }

    public BMPHeader write(WritableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put(magicNumber1);
        buffer.put(magicNumber2);
        buffer.putInt(size);
        buffer.putShort(reserved1);
        buffer.putShort(reserved2);
        buffer.putInt(offset);

        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        dib.write(channel);
        return this;
    }

    public int getSize() {
        return size;
    }

    public short getReserved1() {
        return reserved1;
    }

    public short getReserved2() {
        return reserved2;
    }

    public int getOffset() {
        return offset;
    }

    public BMPDIBHeader getDib() {
        return dib;
    }

    @Override
    public String toString() {
        return String.format(
                "magic: %c%c\n" +
                        "size: %d\n" +
                        "reserved1: %d\n" +
                        "reserved2: %d\n" +
                        "offset: %d",
                magicNumber1, magicNumber2,
                size, reserved1, reserved2, offset);
    }

    public int getImageSize() {
        return imageSize;
    }

    public byte getMagicNumber1() {
        return magicNumber1;
    }

    public byte getMagicNumber2() {
        return magicNumber2;
    }
}
