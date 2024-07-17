package com.example.server.mock;

import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.HttpServerRequestAdapter;

public class BaseRequestMock extends BaseRequest {

    public BaseRequestMock(HttpServerRequestAdapter httpServerRequest, String body) {
        super(httpServerRequest, body);
    }

    public String endResult;
    @Override
    public void end(String text) {
        endResult = text;
    }
}
