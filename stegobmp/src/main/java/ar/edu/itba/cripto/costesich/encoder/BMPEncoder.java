package ar.edu.itba.cripto.costesich.encoder;

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

public abstract class BMPEncoder<T extends Combiner> implements Encoder<T> {
    private static final Logger logger = LoggerFactory.getLogger(BMPEncoder.class);

    @Override
    public void encode(File image, File secret, File output, T combiner) throws IOException {
        var imageChannel = Files.newByteChannel(image.toPath(), StandardOpenOption.READ);
        output.delete();
        output.createNewFile();
        var outputChannel = Files.newByteChannel(output.toPath(), StandardOpenOption.WRITE);

        var header = BMPHeader.read(imageChannel);

        var pixelSize = header.getDib().getBitsPerPixel();
        if (pixelSize != 24) {
            throw new IllegalArgumentException("Pixel size should be 24bits");
        }

        var message = packSecretBytes(secret);
        var newContents = combiner.combineAll(header, imageChannel, message);

        header.write(outputChannel);
        serializeMessage(newContents, outputChannel);
    }

    private void serializeMessage(ReadableByteChannel input, WritableByteChannel output) throws IOException {
        logger.info("Serializing to file");
        // Pipe
        var inputStream = Channels.newInputStream(input);
        var outputStream = Channels.newOutputStream(output);
        inputStream.transferTo(outputStream);
        inputStream.close();
        outputStream.close();
        logger.info("Serialized. Streams closed.");
    }

    protected abstract SecretMessage packSecretBytes(File secret) throws IOException;
}
