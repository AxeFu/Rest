package com.example.server.services.data.base;

import com.example.server.services.promise.Promise;
import io.vertx.core.json.JsonObject;

public abstract class DataBaseService {

    /**
     * Передаёт в promise таблицу в классе JsonObject
     * @param value текст запроса
     * @param promise promise
     */
    public abstract void query(String value, Promise<JsonObject> promise);

    /**
     * Передаёт в promise таблицу класс JsonObject в котором содержится результат за
     * @param tableName имя таблицы
     * @param object объект поля которого похожи на запись в базе данных
     * @param promise promise метода query()
     */
    public abstract void where(String tableName, Object object, Promise<JsonObject> promise);
    /**
     * Собирает запрос на добавление в таблицу базы данных
     * @param tableName имя Таблицы
     * @param object объект у которого поля имеют аннотацию TableField
     * @param promise promise метода query()
     */
    public abstract void addToTable(String tableName, Object object, Promise<JsonObject> promise);

    /**
     * Собирает запрос на изменение строки в таблице базы данных
     * @param tableName имя таблицы
     * @param old старый объект
     * @param update новый объект
     * @param promise promise метода query()
     */
    public abstract void updateTable(String tableName, Object old, Object update, Promise<JsonObject> promise);

}
