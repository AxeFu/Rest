package com.example.server.services.data.base;

import com.example.server.dto.Token;
import com.example.server.dto.User;
import com.example.server.exceptions.AddTableQueryException;
import com.example.server.exceptions.WhereTableQueryException;
import com.example.server.reflection.Reflection;
import com.example.server.services.promise.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DummyDataBaseService extends DataBaseService {
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Token> tokens = new ArrayList<>();
    private Map<String, ArrayList> base = new HashMap<>();

    public DummyDataBaseService(Vertx vertx) {
        base.put("users", users);
        base.put("tokens", tokens);
    }

    @Override
    public void query(String value, Promise<JsonObject> promise) {

    }

    @Override
    public void where(String tableName, Object object, Promise<JsonObject> promise) {
        if (base.get(tableName) == null) {
            promise.reject(new WhereTableQueryException());
        }
        if (object instanceof User) {
            whereUsers((User) object, promise);
            return;
        }
        if (object instanceof Token) {
            whereToken((Token) object, promise);
            return;
        }
        promise.reject(new Exception("Failed to execute query!"));
    }

    @Override
    public void addToTable(String tableName, Object object, Promise<JsonObject> promise) {
        if (base.get(tableName) == null) {
            promise.reject(new AddTableQueryException());
            return;
        }
        if (object instanceof User) {
            addUser((User) object, promise);
            return;
        }
        if (object instanceof Token) {
            addToken((Token) object, promise);
            return;
        }
        promise.reject(new Exception("Failed to execute query!"));
    }

    @Override
    public void updateTable(String tableName, Object old, Object update, Promise<JsonObject> promise) {
        if (base.get(tableName) == null) {
            promise.reject(new AddTableQueryException());
            return;
        }
        if (old instanceof User) {
            whereUsers((User) old, new Promise<>() {
                @Override
                public void success(JsonObject result) {
                    User user = Reflection.createFromJson(result, User.class);
                    User newUser = (User) update;

                    User updatedUser = new User(user.id,
                        newUser.firstName == null ? user.firstName : newUser.firstName,
                        newUser.lastName == null ? user.lastName : newUser.lastName,
                        newUser.login == null ? user.login : newUser.login,
                        newUser.password == null ? user.password : newUser.password
                    );
                    users.remove(user);
                    users.add(updatedUser);
                    promise.success(Reflection.toJsonObject(updatedUser));
                }

                @Override
                public void reject(Exception e) {
                    promise.reject(new Exception("Failed to execute query!"));
                }
            });
            return;
        }
        if (old instanceof Token) {
            whereToken((Token) old, new Promise<>() {
                @Override
                public void success(JsonObject result) {
                    Token token = Reflection.createFromJson(result, Token.class);
                    Token newToken = (Token) update;

                    Token updatedToken = new Token(
                        newToken.authorization == null ? token.authorization : newToken.authorization,
                        newToken.refresh == null ? token.refresh : newToken.refresh,
                        newToken.userId == null ? token.userId : newToken.userId,
                        newToken.time == null ? token.time : newToken.time
                        );

                    tokens.remove(token);
                    tokens.add(updatedToken);
                    promise.success(Reflection.toJsonObject(updatedToken));
                    return;
                }

                @Override
                public void reject(Exception e) {
                    promise.reject(new Exception("Failed to execute query!"));
                }
            });
        }
        promise.reject(new Exception());
    }

    private void whereUsers(User user, Promise<JsonObject> promise) {
        for (User base : users) {
            if (user.login != null && user.password != null)
                if (base.login.equals(user.login) && (base.password.equals(user.password))) {
                    promise.success(Reflection.toJsonObject(base));
                    return;
                }

            if (base.id.equals(user.id)) {
                promise.success(Reflection.toJsonObject(base));
                return;
            }
        }
        promise.reject(new Exception("Failed to execute query!"));
    }

    private void addToken(Token token, Promise<JsonObject> promise) {
        if (token.time == 0 || token.authorization == null || token.refresh == null) {
            promise.reject(new AddTableQueryException("token field is null"));
            return;
        }
        for (Token base : tokens) {
            if (base.authorization.equals(token.authorization) || base.refresh.equals(token.refresh)) {
                promise.reject(new AddTableQueryException());
                return;
            }
        }
        tokens.add(token);
        promise.success(null);
    }

    private void whereToken(Token token, Promise<JsonObject> promise) {
        if (token.refresh != null) {
            for (Token base : tokens) {
                if (base.refresh.equals(token.refresh)) {
                    promise.success(Reflection.toJsonObject(base));
                    return;
                }
            }
        }

        if (token.authorization != null) {
            for (Token base : tokens) {
                if (base.authorization.equals(base.authorization)) {
                    promise.success(Reflection.toJsonObject(base));
                    return;
                }
            }
        }

        promise.reject(new Exception("Failed to execute query!"));
    }

    private void addUser(User user, Promise<JsonObject> promise) {
        if (user.login == null || user.password == null || user.firstName == null || user.lastName == null) {
            promise.reject(new AddTableQueryException("user field is null"));
            return;
        }
        for (User base : users) {
            if (base.login.equals(user.login)) {
                promise.reject(new AddTableQueryException());
                return;
            }
        }
        User newUser = new User((long)(users.size()), user.firstName, user.lastName, user.login, user.password);
        users.add(newUser);
        promise.success(null);
    }
}
