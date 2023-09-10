package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private Map<String, String> headers = new LinkedHashMap<>();
    private HttpStatus httpStatus;
    private String body;
    private Map<String, String> cookies = new ConcurrentHashMap<>();

    public HttpResponse() {
        headers.put(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public HttpResponse(Map<String, String> headers,
                        HttpStatus httpStatus, String body) {
        this.headers = headers;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HttpResponse ok(String responseBody, ContentType contentType) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, contentType.value + ";charset=utf-8");
        headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        return new HttpResponse(headers, HttpStatus.OK, responseBody);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getCookie() {
        return cookies;
    }

    public void setBody(String body) {
        this.body = body;
        headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        headers.put(HttpHeaders.CONTENT_TYPE, ContentType.HTML.value + ";charset=utf-8");
    }

    public void setBody(String body, ContentType contentType) {
        this.body = body;
        headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        headers.put(HttpHeaders.CONTENT_TYPE, contentType.value + ";charset=utf-8");
    }

    public void toRedirect(String redirectUrl) {
        this.httpStatus = HttpStatus.FOUND;
        headers.put(HttpHeaders.LOCATION, redirectUrl);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHeaderAttribute(String key, String value) {
        headers.put(key, value);
    }
}
