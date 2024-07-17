package com.example.server.handlers.implementation;

import com.example.server.dto.Response;
import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.services.data.base.DataBaseService;

/**
 * Хендлер отдаёт текст запроса отправителю
 */
public class PingPongHandler extends ServerHandler {

    public static final String PATH = "ping";

    @Override
    public String getPath() {
        return PATH;
    }

    public PingPongHandler(BaseRequest baseRequest, DataBaseService dataBaseService) {
        super(baseRequest, dataBaseService);
    }

    @Override
    public void didWillExecute() {
        execute();
    }

    @Override
    public void execute() {
        end(body());
    }
}
