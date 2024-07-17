package com.example.server.handlers;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public abstract class HttpServerRequestAdapter {

    private HttpServerRequest httpServerRequest;
    public HttpServerRequestAdapter(HttpServerRequest httpServerRequest) {
        this.httpServerRequest = httpServerRequest;
    }

    public void end(Object obj) {
        response().end(obj.toString());
    }

    public void end() {
        response().end();
    }

    public String query() {
        return httpServerRequest.query();
    }

    private HttpServerResponse response() {
        return  httpServerRequest.response();
    }

}
