package com.example.server.handlers;

import com.example.server.dto.User;
import com.example.server.services.data.base.DataBaseService;

public abstract class BaseAuthHandler extends ServerHandler {
    public BaseAuthHandler(BaseRequest baseRequest, DataBaseService dataBaseService, User user) {
        super(baseRequest, dataBaseService);
    }


}
