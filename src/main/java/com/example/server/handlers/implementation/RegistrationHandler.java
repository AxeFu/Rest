package com.example.server.handlers.implementation;

import com.example.server.dto.Response;
import com.example.server.dto.User;
import com.example.server.exceptions.RegistrationException;
import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.log.Logger;
import com.example.server.reflection.Reflection;
import com.example.server.services.data.base.DataBaseService;
import com.example.server.services.promise.Promise;
import io.vertx.core.json.JsonObject;

public class RegistrationHandler extends ServerHandler {

    public static final String PATH = "registration";

    @Override
    public String getPath() {
        return PATH;
    }

    public RegistrationHandler(BaseRequest baseRequest, DataBaseService dataBaseService) {
        super(baseRequest, dataBaseService);
    }
    @Override
    public void didWillExecute() {
        execute();
    }

    /**
     * Новый пользователь записывается в базу
     */
    @Override
    public void execute() {
        User newUser = Reflection.createFromJson(body(), User.class);
        getDataBaseService().addToTable("users", newUser, new Promise<>() {
            @Override
            public void success(JsonObject result) {
                Logger.print("Пользователь зарегистрирован! " + newUser.login);
                end(Response.SUCCESS.setData(newUser));
            }

            @Override
            public void reject(Exception e) {
                Logger.print("Пользователь с таким логином уже существует!");
                end(Response.FAILED.setErrorMessage(new RegistrationException()));
            }
        });
    }
}
