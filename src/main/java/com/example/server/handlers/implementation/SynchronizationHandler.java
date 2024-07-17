package com.example.server.handlers.implementation;

import com.example.server.dto.ServiceInfo;
import com.example.server.dto.Response;
import com.example.server.dto.Token;
import com.example.server.exceptions.MethodNotFoundException;
import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.reflection.Reflection;
import com.example.server.services.authorization.UserService;
import com.example.server.services.data.base.DataBaseService;
import com.example.server.services.promise.Promise;
import io.vertx.core.json.JsonObject;

public class SynchronizationHandler extends ServerHandler {

    public static final long maxTime = (long)(1e12)*5*24*3600;
    public static final String PATH = "synchronization";
    private Token token;
    public SynchronizationHandler(BaseRequest baseRequest, DataBaseService dataBaseService) {
        super(baseRequest, dataBaseService);
    }
    @Override
    public void didWillExecute() {
        Token input = Reflection.createFromJson(body(), Token.class);
        String head = head();
        if (head != null) {
            ServiceInfo info = Reflection.createFromJson(head(), ServiceInfo.class);
            if (info.serviceName == null || info.command == null) {
                end(Response.SERVICE_NAME_NOT_FOUND);
                return;
            }
        }
        getDataBaseService().where("tokens", input, new Promise<>() {
            @Override
            public void success(JsonObject result) {
                if (result.isEmpty()) {
                    end(Response.TOKEN_NOT_FOUND);
                    return;
                }
                Token token = Reflection.createFromJson(result, Token.class);
                if (input.refresh != null) {
                    Token newToken = Token.create(token.userId);
                    getDataBaseService().updateTable("tokens", token, newToken, new Promise<>() {
                        @Override
                        public void success(JsonObject result) {
                            end(Response.SUCCESS.setData(newToken));
                        }

                        @Override
                        public void reject(Exception e) {
                            didWillExecute();
                        }
                    });
                    return;
                }
                if (System.nanoTime() - token.time > maxTime) {
                    end(Response.TOKEN_OUT_DATE);
                    return;
                }
                SynchronizationHandler.this.token = token;
                execute();
            }

            @Override
            public void reject(Exception e) {
                end(Response.FAILED.setErrorMessage(e));
            }
        });
    }

    @Override
    public void execute() {
        Promise<Object> standartPromise = new Promise<>() {
            @Override
            public void success(Object result) {
                end(Response.SUCCESS.setData(result));
            }

            @Override
            public void reject(Exception e) {
                end(Response.FAILED.setErrorMessage(e));
            }
        };

        ServiceInfo head = Reflection.createFromJson(head(), ServiceInfo.class);
        switch (head.serviceName) {
            case "users": {
                UserService service = new UserService(getDataBaseService(), token.userId, standartPromise);
                try {
                    Reflection.Invoke(head.command, body(), service);
                } catch (MethodNotFoundException e) {
                    end(Response.COMMAND_NOT_FOUND);
                }
            } return;
        }
        end(Response.SERVICE_NAME_NOT_FOUND);
    }

    @Override
    public String getPath() {
        return PATH;
    }
}
