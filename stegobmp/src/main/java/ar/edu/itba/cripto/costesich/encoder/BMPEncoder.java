package ar.edu.itba.cripto.costesich.encoder;

import ar.edu.itba.cripto.costesich.bmp.BMPHeader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
public abstract class BMPEncoder<T extends Combiner> implements Encoder<T> {
    @Override
    public void encode(File image, File secret, File output, T combiner) throws IOException {
        var imageChannel = Files.newByteChannel(image.toPath(), StandardOpenOption.READ);
        output.createNewFile();
        var outputChannel = Files.newByteChannel(output.toPath(), StandardOpenOption.WRITE);

        var header = BMPHeader.read(imageChannel);

        header.write(outputChannel);

        var imageSize = Math.max(header.getDib().getImageSize(), header.getSize() - header.getOffset());
        var pixelSize = header.getDib().getBitsPerPixel();
        System.out.println(imageSize);
        System.out.println(pixelSize);

        imageChannel.position(header.getOffset());
        var buffer = ByteBuffer.allocate(imageSize);
        imageChannel.read(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            outputChannel.write(buffer);
        }
    }

    protected abstract InputStream packSecretBytes(File secret) throws IOException;
}
