package com.example.server.dto;

import com.example.server.exceptions.*;
import com.example.server.reflection.Reflection;
import io.vertx.core.json.JsonObject;

public class Response {

    public static final Response SUCCESS = new Response();
    public static final Response FAILED = new Response(-1);
    public static final Response PATH_NOT_FOUND = new Response(404, new PathNotFoundException());
    public static final Response TOKEN_OUT_DATE = new Response(-2, new TokenOutDateException());
    public static final Response SERVICE_NAME_NOT_FOUND = new Response(-3, new ServiceNameNotFoundException());
    public static final Response COMMAND_NOT_FOUND = new Response(-4, new CommandNotFoundException());
    public static final Response TOKEN_NOT_FOUND = new Response(-5, new TokenNotFoundException());
    public final Integer errorCode;
    public final String errorMessage;
    public final String data;

    private Response() {
        this(null, null, null);
    }
    private Response(String data) {
        this(null, null, data);
    }
    private Response(Integer errorCode) {
        this(errorCode, null, null);
    }
    private Response(Integer errorCode, Exception exception) {
        this.errorCode = errorCode;
        this.errorMessage = exception.toString();
        this.data = null;
    }

    private Response(Integer errorCode, String errorMessage, String data) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public Response setData(Object object) {
        Builder builder = builder();
        builder.data = Reflection.toJsonObject(object).toString();
        return builder.build();
    }

    public Response setData(String string) {
        Builder builder = builder();
        builder.data = string;
        return builder.build();
    }

    public Response setData(JsonObject jsonObject) {
        Builder builder = builder();
        builder.data = jsonObject.toString();
        return builder.build();
    }

    public Response setErrorMessage(Exception e) {
        Builder builder = builder();
        builder.errorMessage = e.toString();
        return builder.build();
    }

    public Response setErrorMessage(String message) {
        Builder builder = builder();
        builder.errorMessage = message;
        return builder.build();
    }

    private Builder builder() {
        return new Builder();
    }

    private class Builder {
        public String errorMessage;
        public String data;

        public Builder() {
            errorMessage = Response.this.errorMessage;
            data = Response.this.data;
        }
        public Response build() {
            return new Response(errorCode, errorMessage, data);
        }
    }

}
