package com.example.server.handlers;

import com.example.server.exceptions.PathNullException;
import com.example.server.log.Logger;
import com.example.server.reflection.Reflection;
import com.example.server.services.data.base.DataBaseService;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerHandler implements BaseChainHandler {

    private static final List<String> pathList = new ArrayList<>();
    public static String[] getPaths() {
        String[] paths = new String[pathList.size()];
        pathList.toArray(paths);
        return paths;
    }

    private final BaseRequest baseRequest;
    private final DataBaseService dataBaseService;

    public ServerHandler(BaseRequest baseRequest, DataBaseService dataBaseService) {
        this.baseRequest = baseRequest;
        this.dataBaseService = dataBaseService;
        pathList.add(getPath());
    }

    public DataBaseService getDataBaseService() {
        return dataBaseService;
    }

    /**
     * Метод вызываемый перед execute() чтобы подготовить запрос к обработке, должен содержать в себе вызов execute();
     */
    public abstract void didWillExecute();

    public abstract void execute();

    public void execute(String path) {
        if (path == null || getPath() == null) {
            end(new PathNullException());
            return;
        }
        if (path.equals(getPath())) {
            Logger.print(getPath() + '\n' + head() + '\n' + body());
            didWillExecute();
        }
    }

    /**
     * Вернуть ответ на запрос в виде текста
     * @param obj вызов toString()
     */
    public void end(Object obj) {
        baseRequest.end(Reflection.toJsonObject(obj).toString());
    }

    public void end(JsonObject json) {
        baseRequest.end(json.toString());
    }

    public void end(String string) {
        baseRequest.end(string);
    }

    public void end(Exception e) {
        baseRequest.end(e.toString());
    }

    public void end() {
        baseRequest.end();
    }

    public String body() {
        return baseRequest.body();
    }

    /**
     * Получить текст запроса
     * @return запрос вида key1=value2&key2=value2&...
     */
    public String head() {
        String head = baseRequest.query();
        if (head == null) return null;
        String[] parameters = head.split("&");
        JsonObject result = new JsonObject();
        for (String parameter : parameters) {
            String[] keyValue = parameter.split("=");
            if (keyValue.length > 1) {
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result.toString();
    }

    public abstract String getPath();

}
