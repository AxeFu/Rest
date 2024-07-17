package com.example.server.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class AccessibleField {

    private Field field;

    AccessibleField(java.lang.reflect.Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }
    public Object get(Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return field.getName();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return field.getAnnotation(annotation);
    }

}
