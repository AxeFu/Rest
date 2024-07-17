package com.example.server.mock;

import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.services.data.base.DataBaseService;

public class ServerHandlerMock extends ServerHandler {

    public ServerHandlerMock(BaseRequest baseRequest, DataBaseService dataBaseService) {
        super(baseRequest, dataBaseService);
    }

    private String path;
    public ServerHandlerMock(String path) {
        this(null, null);
        this.path = path;
    }

    @Override
    public void didWillExecute() {

    }

    @Override
    public void execute() {

    }

    @Override
    public String getPath() {
        return path;
    }
}
