package com.example.server.mock;

import com.example.server.services.data.base.DataBaseService;
import com.example.server.services.promise.Promise;
import io.vertx.core.json.JsonObject;

public class DataBaseServiceMock extends DataBaseService {
    @Override
    public void query(String value, Promise<JsonObject> promise) {

    }

    @Override
    public void where(String tableName, Object object, Promise<JsonObject> promise) {

    }

    @Override
    public void addToTable(String tableName, Object object, Promise<JsonObject> promise) {

    }

    @Override
    public void updateTable(String tableName, Object old, Object update, Promise<JsonObject> promise) {

    }
}
