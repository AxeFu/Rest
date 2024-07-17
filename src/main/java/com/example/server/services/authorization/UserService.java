package com.example.server.services.authorization;

import com.example.server.builders.Hash;
import com.example.server.dto.User;
import com.example.server.reflection.Reflection;
import com.example.server.services.data.base.DataBaseService;
import com.example.server.services.promise.Promise;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserService {
    private static final Map<Long, User> users = new HashMap<>();
    private final DataBaseService dataBaseService;
    private final Long userId;
    private Promise<Object> promise;
    public UserService(DataBaseService dataBaseService, Long userId, Promise<Object> promise) {
        this.dataBaseService = dataBaseService;
        this.promise = promise;
        this.userId = userId;
    }
    public void getUser() {
        {
            User user;
            if ((user = users.get(userId)) != null) {
                promise.success(user);
                return;
            }
        }
        dataBaseService.where("users",
            new User(userId, null, null, null, null), new Promise<>() {
                @Override
                public void success(JsonObject result) {
                    User user = Reflection.createFromJson(result, User.class);
                    user.password = null;
                    users.put(userId, user);
                    promise.success(user);
                }

                @Override
                public void reject(Exception e) {
                    promise.reject(e);
                }
            });
    }
    public void setUser(User user) {
        Promise<Object> save = promise;
        this.promise = new Promise<>() {
            @Override
            public void success(Object old) {
                promise = save;
                dataBaseService.updateTable("users", old, user, new Promise<>() {
                    @Override
                    public void success(JsonObject result) {
                        User updatedUser = Reflection.createFromJson(result, User.class);
                        updatedUser.password = null;
                        users.put(userId, updatedUser);
                        promise.success(updatedUser);
                    }

                    @Override
                    public void reject(Exception e) {
                        promise.reject(e);
                    }
                });
            }
            @Override
            public void reject(Exception e) {
                promise = save;
                promise.reject(e);
            }
        };
        getUser();
    }
}
