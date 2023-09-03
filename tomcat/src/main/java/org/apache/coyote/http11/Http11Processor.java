package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import nextstep.jwp.ContentType;
import nextstep.jwp.HttpRequest;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(inputStream);
            if (request.getTarget().equals("/")) {
                final var response = getResponse("Hello world!", ContentType.HTML);
                outputStream.write(response.getBytes());
                outputStream.flush();
            } else {
                String fileUrl = "static" + request.getTarget();
                File file = new File(
                    getClass()
                        .getClassLoader()
                        .getResource(fileUrl)
                        .getFile()
                );
                String responseBody = new String(Files.readAllBytes(file.toPath()));
                String response = getResponse(responseBody, ContentType.from(file.getName()));
                outputStream.write(response.getBytes());
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getResponse(String responseBody, ContentType contentType) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: "+ contentType.value  + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
                "",
            responseBody);
    }
}
