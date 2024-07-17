package com.example.server;

import com.example.server.exceptions.BodyIsNotJsonException;
import com.example.server.handlers.BaseHttpServerRequest;
import com.example.server.handlers.BaseRequest;
import com.example.server.handlers.ServerHandler;
import com.example.server.handlers.implementation.*;
import com.example.server.services.data.base.DataBaseService;
import com.example.server.services.data.base.DummyDataBaseService;
import com.example.server.services.data.base.MySQLMariadbDataBaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class ServerVerticle extends AbstractVerticle {

    /**
     * @param baseRequest класс контейнер хранящий HttpRequestHandler
     * @param dataBaseService база данных
     * @return лист ServerHandler-ов для обработки запросов
     */
    public List<ServerHandler> getServerHandlers(BaseRequest baseRequest, DataBaseService dataBaseService) {
        return List.of(
            new SynchronizationHandler(baseRequest, dataBaseService),
            new AuthorizationHandler(baseRequest, dataBaseService),
            new RegistrationHandler(baseRequest, dataBaseService),
            new PingPongHandler(baseRequest, dataBaseService),
            new PathNotFoundHandler(baseRequest, dataBaseService)
        );
    }

    /**
     * Развёртка сервера, точка входа программы
     */
    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(req -> req.body(bufferAsyncResult -> {
            String path = req.path();
            //убираем символ / в начале path
            path = path.substring(1);
            String body = bufferAsyncResult.result().toString();
            BaseHttpServerRequest httpServerRequest = new BaseHttpServerRequest(req);
            try {
                new JsonObject(body);
            } catch (Exception e) {
                httpServerRequest.end(new BodyIsNotJsonException(e.getMessage()));
                return;
            }
            BaseRequest baseRequest = new BaseRequest(httpServerRequest, body);
            DataBaseService dataBaseService = new MySQLMariadbDataBaseService(vertx);
            for (ServerHandler serverHandler : getServerHandlers(baseRequest, dataBaseService)) {
                serverHandler.execute(path);
            }
        })).listen(8888, http -> {
            if (http.succeeded()) {
                System.out.println("HTTP сервер запущен");
            }
        });
    }
}
