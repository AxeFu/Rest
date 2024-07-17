package com.example.server.mock;

import com.example.server.handlers.HttpServerRequestAdapter;

public class HttpServerRequestMock extends HttpServerRequestAdapter {
    public HttpServerRequestMock() {
        super(null);
    }

    @Override
    public String query() {
        return "ResultQuery";
    }
}
