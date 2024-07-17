package com.example.server.handlers;

public class BaseRequest {

    private final HttpServerRequestAdapter httpServerRequest;
    private final String body;

    public BaseRequest(HttpServerRequestAdapter httpServerRequest, String body) {
        this.httpServerRequest = httpServerRequest;
        this.body = body;
    }

    public HttpServerRequestAdapter getHttpServerRequest() {
        return httpServerRequest;
    }

    public void end(String text) {
        httpServerRequest.end(text);
    }

    public void end() {
        httpServerRequest.end();
    }

    public String query() {
        return httpServerRequest.query();
    }

    public String body() {
        return body;
    }

}
