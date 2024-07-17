package com.example.server.services.data.base;

import com.example.server.log.Logger;
import com.example.server.reflection.AccessibleField;
import com.example.server.reflection.Reflection;
import com.example.server.reflection.annotations.TableField;
import com.example.server.services.promise.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.example.server.services.data.base.BaseConfig.*;

public class MySQLMariadbDataBaseService extends DataBaseService {

    private final static MySQLConnectOptions connectOptions = new MySQLConnectOptions()
        .setPort(PORT)
        .setHost(HOST)
        .setDatabase(DATA_BASE)
        .setUser(USER)
        .setPassword(PASSWORD);

    private final MySQLPool pool;

    public MySQLMariadbDataBaseService(Vertx vertx) {
        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
        pool = MySQLPool.pool(vertx, connectOptions, poolOptions);
    }

    @Override
    public void query(String value, Promise<JsonObject> promise) {
        pool.getConnection()
            .compose(connection -> connection.query(value).execute().onComplete(ar -> connection.close()))
            .onComplete(ar -> {
                if (ar.succeeded()) {
                    JsonObject json = new JsonObject();
                    ar.result().forEach(row -> {
                        int count = row.deepToString().split(",").length;
                        for (int i = 0; i < count; i++) {
                            json.put(row.getColumnName(i), row.getValue(i));
                        }
                    });
                    promise.success(json);
                } else {
                    promise.reject(new Exception("Failed to execute query!"));
                    Logger.error("Query: " + value);
                    Logger.error("Something went wrong " + ar.cause().getMessage());
                }
            });
    }

    @Override
    public void where(String tableName, Object object, Promise<JsonObject> promise) {
        this.query(getWhere(tableName, object), promise);
    }

    @Override
    public void addToTable(String tableName, Object object, Promise<JsonObject> promise) {
        AccessibleField[] fields = Reflection.getAnnotatedFields(object, TableField.class);
        List<AccessibleField> notKeyFields = new ArrayList<>();
        for (AccessibleField field : fields) {
            if (field.getAnnotation(TableField.class).isKey()){
                continue;
            }
            notKeyFields.add(field);
        }
        Function<AccessibleField, String> into = field -> {
            TableField tableField = field.getAnnotation(TableField.class);
            return '`' + tableField.value() + '`';
        };
        Function<AccessibleField, String> values = field -> {
            Object value = field.get(object);
            if (value == null) {
                return "'null'";
            }
            return '\'' + value.toString() + '\'';
        };
        StringBuilder query = new StringBuilder("INSERT INTO ")
            .append('`').append(BaseConfig.DATA_BASE).append('`')
            .append('.').append('`').append(tableName).append('`')
            .append(' ').append('(')
            .append(Reflection.arrayToSeparatedStringBuilder(notKeyFields, into, ", "))
            .append(") VALUES (")
            .append(Reflection.arrayToSeparatedStringBuilder(notKeyFields, values, ", "))
            .append(')').append(';');
        query(query.toString(), promise);
    }
    @Override
    public void updateTable(String tableName, Object old, Object update, Promise<JsonObject> promise) {
        AccessibleField[] updateFields = Reflection.getAnnotatedFields(update, TableField.class);
        AccessibleField[] oldFields = Reflection.getAnnotatedFields(old, TableField.class);

        List<AccessibleField> uniqueFields = new ArrayList<>();
        List<AccessibleField> notNullOld = new ArrayList<>();

        start:
        for (AccessibleField updateField : updateFields) {
            for (AccessibleField oldField : oldFields) {
                TableField oldTableField = oldField.getAnnotation(TableField.class);
                TableField updateTableField = updateField.getAnnotation(TableField.class);

                if (oldTableField.value().equals(updateTableField.value())) {
                    Object oldValue = oldField.get(old);
                    Object updateValue = updateField.get(update);

                    if ((updateValue == null)) {
                        continue start;
                    }

                    if (updateValue.equals(oldValue)) {
                        continue start;
                    }
                }
            }
            uniqueFields.add(updateField);
        }

        if (uniqueFields.isEmpty()) {
            promise.success(Reflection.toJsonObject(old));
            return;
        }

        for (AccessibleField oldField : oldFields) {
            if (oldField.get(old) != null) {
                notNullOld.add(oldField);
            }
        }

        Function<AccessibleField, String> set = field -> {
            TableField tableField = field.getAnnotation(TableField.class);
            return '`' + tableField.value() + "` = '" + field.get(update) + '\'';
        };

        Function<AccessibleField, String> where = field -> {
            TableField tableField = field.getAnnotation(TableField.class);
            return '`' + tableField.value() + "` = '" + field.get(old) + '\'';
        };

        StringBuilder query = new StringBuilder("UPDATE ")
            .append('`').append(DATA_BASE).append('`').append('.').append('`').append(tableName).append('`')
            .append(" SET ")
            .append(Reflection.arrayToSeparatedStringBuilder(uniqueFields, set, ", "))
            .append(" WHERE ").append('(')
            .append(Reflection.arrayToSeparatedStringBuilder(notNullOld, where, " and "))
            .append(')');
        query(query.toString(), new Promise<>() {
            @Override
            public void success(JsonObject result) {
                query(getWhereOnlyKey(tableName, old), promise);
            }

            @Override
            public void reject(Exception e) {
                promise.reject(e);
            }
        });
    }

    private String getWhere(String tableName, Object object) {
        AccessibleField[] annotatedFields = Reflection.getAnnotatedFields(object, TableField.class);
        List<AccessibleField> notNulllist = new ArrayList<>();
        for (AccessibleField field : annotatedFields) {
            if (field.get(object) != null) {
                notNulllist.add(field);
            }
        }

        Function<AccessibleField, String> where = field -> {
            String tableFieldName = field.getAnnotation(TableField.class).value();
            return tableFieldName + " = '" + field.get(object) + "'";
        };

        StringBuilder query = new StringBuilder()
            .append("SELECT * FROM `").append(DATA_BASE).append('`').append(".`").append(tableName).append("`")
            .append(" WHERE ")
            .append(Reflection.arrayToSeparatedStringBuilder(notNulllist, where, " and "));
        return query.toString();
    }

    private String getWhereOnlyKey(String tableName, Object object) {
        AccessibleField[] annotatedFields = Reflection.getAnnotatedFields(object, TableField.class);
        List<AccessibleField> notNullKeyList = new ArrayList<>();
        for (AccessibleField field : annotatedFields) {
            if (field.get(object) != null) {
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField.isKey()) {
                    notNullKeyList.add(field);
                }
            }
        }

        Function<AccessibleField, String> where = field -> {
            String tableFieldName = field.getAnnotation(TableField.class).value();
            return tableFieldName + " = '" + field.get(object) + "'";
        };

        StringBuilder query = new StringBuilder()
            .append("SELECT * FROM `").append(DATA_BASE).append('`').append(".`").append(tableName).append("`")
            .append(" WHERE ")
            .append(Reflection.arrayToSeparatedStringBuilder(notNullKeyList, where, " and "));
        return query.toString();
    }
}
