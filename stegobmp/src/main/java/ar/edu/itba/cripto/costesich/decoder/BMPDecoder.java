package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public abstract class BMPDecoder<T extends Splitter> implements Decoder<T> {
    private static final Logger logger = LoggerFactory.getLogger(BMPDecoder.class);

    @Override
    public String decode(File image, File output, T splitter) throws IOException {
        var imageChannel = Files.newByteChannel(image.toPath(), StandardOpenOption.READ);
        output.delete();
        output.createNewFile();
        var outputChannel = Files.newByteChannel(output.toPath(), StandardOpenOption.WRITE);

        var header = BMPHeader.read(imageChannel);

        var pixelSize = header.getDib().getBitsPerPixel();
        if (pixelSize != 24) {
            throw new IllegalArgumentException("Pixel size should be 24bits");
        }

        var message = recompose(splitter.splitAll(header, imageChannel), header);
        serializeMessage(message, outputChannel);
        return message.getExtension();
    }


    abstract protected SecretMessage recompose(ReadableByteChannel secret, BMPHeader header) throws IOException;

    private void serializeMessage(SecretMessage message, WritableByteChannel output) throws IOException {
        logger.info("Serializing to file");
        // Pipe
        var in = message.getStream();
        var out = Channels.newOutputStream(output);
        in.transferTo(out);
        in.close();
        out.close();
        logger.info("File saved. Streams closed.");
    }
}
