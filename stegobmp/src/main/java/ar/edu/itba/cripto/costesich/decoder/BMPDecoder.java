package ar.edu.itba.cripto.costesich.decoder;

import ar.edu.itba.cripto.costesich.SecretMessage;
import ar.edu.itba.cripto.costesich.bmp.BMPHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
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
        var ext = serializeMessage(message, outputChannel);
        var size = message.getSize();
        var name = output.toPath().getFileName();
        logger.info("Wrote {} bytes to {}, which should have extension {}.", size, name, ext);
        return ext;
    }


    abstract protected SecretMessage recompose(ReadableByteChannel secret, BMPHeader header) throws IOException;


    private int transferNBytes(int size, ReadableByteChannel file, WritableByteChannel output) throws IOException {
        var remaining = size;
        var total = 0;
        while (remaining > 0) {
            var buffer = ByteBuffer.allocateDirect(Math.min(remaining, 8192));
            var written = 0;
            if (file.read(buffer) != -1) {
                // prepare the buffer to be drained
                buffer.flip();
                // write to the channel, may block
                written = output.write(buffer);

                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }

            // EOF will leave buffer in fill state
            buffer.flip();

            // make sure the buffer is fully drained.
            while (buffer.hasRemaining()) {
                var i = output.write(buffer);
                if (i == -1) {
                    throw new IOException("oops");
                }
                written += i;
            }
            remaining -= written;
            total += written;
        }
        return total;
    }

    private String serializeMessage(SecretMessage message, WritableByteChannel output) throws IOException {
        logger.info("Serializing to file");
        if (message.getExtension() == null) {
            logger.info("Using streams");
            //extract extension from stream
            var file = Channels.newChannel(message.getStream());
            transferNBytes(message.getSize(), file, output);
            output.close();
            return readFileExtension(file);
        }

        // Pipe
        var in = message.getStream();
        var out = Channels.newOutputStream(output);
        in.transferTo(out);
        in.close();
        out.close();
        logger.info("File saved. Streams closed.");
        var ext = message.getExtension();
        return ext;
    }

    protected String readFileExtension(ReadableByteChannel channel) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.SIZE);
        do {
            if (channel.read(buffer) == -1) {
                break;
            }
        } while (buffer.position() < buffer.capacity());
        buffer.flip();
        var raw = new String(buffer.array());
        return raw.substring(0, Math.max(raw.indexOf(0), 0));
    }
}
