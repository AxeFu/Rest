package com.example.server.dto;

import com.example.server.builders.Hash;
import com.example.server.reflection.annotations.TableField;

public class Token {
    @TableField(value = "id", isKey = true)
    public Long id;
    @TableField("auth_token")
    public final String authorization;
    @TableField("refresh_token")
    public final String refresh;
    @TableField("user_id")
    public final Long userId;
    @TableField("time")
    public final Long time;

    public Token(String authorization, String refresh, Long userId, Long time) {
        this.authorization = authorization;
        this.refresh = refresh;
        this.userId = userId;
        this.time = time;
    }

    public static Token create(Long userId) {
        return new Token(Hash.getRandom(), Hash.getRandom(), userId, System.nanoTime());
    }

    @Override
    public String toString() {
        return "Auth: " +  authorization + " Refresh: " + refresh + " UserId: " + userId;
    }

}
