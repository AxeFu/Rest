package com.example.server.services.promise;

public abstract class Promise<T> {
    public abstract void success(T result);

    public abstract void reject(Exception e);
}
