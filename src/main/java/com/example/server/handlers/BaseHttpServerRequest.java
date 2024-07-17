package com.example.server.handlers;

import io.vertx.core.http.HttpServerRequest;

public class BaseHttpServerRequest extends HttpServerRequestAdapter{
    public BaseHttpServerRequest(HttpServerRequest httpServerRequest) {
        super(httpServerRequest);
    }
}
