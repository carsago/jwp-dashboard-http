package nextstep.jwp;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    @Test
    void HTTP_요청의_uri를_반환한다() throws IOException {
        // given
        String httpRequest= String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        String actual = request.getUri();

        // then
        assertThat(actual).isEqualTo("/index.html");
    }

    @Test
    void uri는_query_string_없이_반환한다() throws IOException {
        // given
        // given
        String httpRequest= String.join("\r\n",
            "GET /blackCat?age=30&name=wooseok HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = new HttpRequest(inputStream);

        // when
        String actual = request.getUri();

        // then
        assertThat(actual).isEqualTo("/blackCat");
    }
}
