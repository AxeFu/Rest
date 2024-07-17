package com.example.server.handlers.implementation;

import com.example.server.dto.Response;
import com.example.server.dto.Token;
import com.example.server.dto.User;
import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.exceptions.AuthorizationException;
import com.example.server.log.Logger;
import com.example.server.reflection.Reflection;
import com.example.server.services.data.base.DataBaseService;
import com.example.server.services.promise.Promise;
import io.vertx.core.json.JsonObject;

import java.sql.Ref;

public class AuthorizationHandler extends ServerHandler {

    public static final String PATH = "authorization";

    @Override
    public String getPath() {
        return PATH;
    }

    public AuthorizationHandler(BaseRequest baseRequest, DataBaseService dataBaseService) {
        super(baseRequest, dataBaseService);
    }

    @Override
    public void didWillExecute() {
        User user = Reflection.createFromJson(body(), User.class);
        if (user.login == null || user.password == null) {
            end(Response.FAILED.setErrorMessage(new AuthorizationException()));
            return;
        }
        getDataBaseService().where("users", user, new Promise<>() {
            @Override
            public void success(JsonObject result) {
                if (result.isEmpty()) {
                    end(Response.FAILED.setErrorMessage(new AuthorizationException()));
                    return;
                }
                AuthorizationHandler.this.result = result;
                execute();
                Logger.print(String.format("Пользователь: %s вошел в систему", user.login));
            }
            @Override
            public void reject(Exception e) {
                end(Response.FAILED.setErrorMessage(new AuthorizationException()));
                Logger.print("Неправильный логин или пароль! " + e);
            }
        });
    }

    /**
     * Авторизация
     */
    private JsonObject result;
    @Override
    public void execute() {
        Token newToken = Token.create(result.getLong("id"));
        System.out.println(newToken.userId);
        getDataBaseService().addToTable("tokens", newToken, new Promise<>() {
            @Override
            public void success(JsonObject result) {
                end(Response.SUCCESS.setData(newToken));
                Logger.print("Пользователь авторизован! id: " + newToken.userId);
            }

            @Override
            public void reject(Exception e) {
                execute();
                Logger.error("Совпадение ключей класса Hash! " + e);
            }
        });
    }
}
