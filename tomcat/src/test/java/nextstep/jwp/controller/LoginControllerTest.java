package nextstep.jwp.controller;

import static nextstep.jwp.utils.Constant.SESSION_ID;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import nextstep.jwp.handler.DashboardException;
import nextstep.jwp.utils.FileFinder;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Nested
    class 로그인_요청시 {

        @Test
        void 로그인을_성공한다() throws Exception {
            // given
            HttpRequest request = createRequest(HttpMethod.POST, "account=gugu&password=password");
            HttpResponse response = new HttpResponse();

            // when
            loginController.doPost(request, response);

            // then
            String sessionKey = response.getCookie().get(SESSION_ID);
            assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
                () -> assertThat(response.getCookie()).containsKey(SESSION_ID),
                () -> assertUserInSession(request, sessionKey)
            );
        }

        @Test
        void 존재하지않는_회원이면_예외() {
            // given
            HttpRequest request = createRequest(HttpMethod.POST, "account=hi&password=password");
            HttpResponse response = new HttpResponse();

            // when & then
            assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(DashboardException.class);
        }

        @Test
        void 비밀번호가_틀리면_예외() throws Exception {
            // given
            HttpRequest request = createRequest(HttpMethod.POST, "account=hi&password=fail");
            HttpResponse response = new HttpResponse();

            // when & then
            assertThatThrownBy(() -> loginController.doPost(request, response))
                .isInstanceOf(DashboardException.class);
        }
    }

    @Test
    void 로그인_페이지로_요청() throws Exception {
        // given
        HttpRequest request = createRequest(HttpMethod.GET, "");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(FileFinder.getFileContent("/login.html"));
    }

    private ObjectAssert<Object> assertUserInSession(HttpRequest request, String sessionKey) {
        return assertThat(request.getSessionManager().findSession(sessionKey).getAttribute("user")).isNotNull();
    }

    private HttpRequest createRequest(HttpMethod method, String body) {
        return new HttpRequest(
            new HttpRequestLine(method, "/register", "version"),
            Map.of(),
            Map.of(),
            Map.of(),
            body
        );
    }
}
