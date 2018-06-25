package ar.edu.itba.cripto.costesich.bmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;

public class BMPHeader {
    private static final Logger logger = LoggerFactory.getLogger(BMPDIBHeader.class);

    private static final int HEADER_SIZE = 14;
    private final short magicNumber;
    private final int size;
    private final short reserved1;
    private final short reserved2;
    private final int offset;
    private final BMPDIBHeader dib;


    private BMPHeader(short magicNumber, int size, short reserved1, short reserved2, int offset, BMPDIBHeader dib) {
        this.magicNumber = magicNumber;
        this.size = size;
        this.reserved1 = reserved1;
        this.reserved2 = reserved2;
        this.offset = offset;
        this.dib = dib;
        logger.info("Created base header with the following data:\n{}", toString());
    }

    public static BMPHeader read(ReadableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        channel.read(buffer);
        buffer.rewind();
        short magicNumber = buffer.getShort();
        int size = buffer.getInt();
        short reserved1 = buffer.getShort();
        short reserved2 = buffer.getShort();
        int offset = buffer.getInt();

        BMPDIBHeader dib = BMPDIBHeader.read(channel);
        return new BMPHeader(magicNumber, size, reserved1, reserved2, offset, dib);
    }

    public BMPHeader write(WritableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putShort(magicNumber);
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

    public short getMagicNumber() {
        return magicNumber;
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
                "magic: 0x%h\n" +
                "size: %d\n" +
                "reserved1: %d\n" +
                "reserved2: %d\n" +
                "offset: %d",
                magicNumber,
                size, reserved1, reserved2, offset);
    }
}
