package com.example.server.handlers.implementation;

import com.example.server.dto.Response;
import com.example.server.exceptions.PathNotFoundException;
import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.services.data.base.DataBaseService;

public class PathNotFoundHandler extends ServerHandler {

    public PathNotFoundHandler(BaseRequest baseRequest, DataBaseService dataBaseService) {
        super(baseRequest, dataBaseService);
    }

    @Override
    public void execute(String currentPath) {
        for (String path : getPaths()) {
            if (path.equals(currentPath)) {
                return;
            }
        }
        end(Response.PATH_NOT_FOUND);
    }

    @Override
    public void didWillExecute() {
    }

    @Override
    public void execute() {
    }

    @Override
    public String getPath() {
        return "null";
    }
}
