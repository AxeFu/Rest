package com.example.server.reflection;

import com.example.server.exceptions.MethodNotFoundException;
import com.example.server.reflection.annotations.TableField;
import io.vertx.core.json.JsonObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused"})
public class Reflection {

    /**
     * Метод для получения всех публичных полей класса
     * @param object целевой объекта
     * @return все публичные поля целевого объекта
     */
    public static AccessibleField[] getFields(Object object) {
        Class<?> klass = object.getClass();
        List<Field> result = new ArrayList<>();
        for (Field field : klass.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                result.add(field);
            }
        }
        AccessibleField[] fields = new AccessibleField[result.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new AccessibleField(result.get(i));
        }
        return fields;
    }

    /**
     * Метод для получения всех полей класса включая приватные
     * @param object целевой объект
     * @return поля целевого объекта
     */
    public static Field[] getDeclaredFields(Object object) {
        Class<?> klass = object.getClass();
        List<Field> result = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                result.add(field);
            }
        }
        Field[] fields = new Field[result.size()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = result.get(i);
        }
        return fields;
    }

    /**
     * Метод получает все аннотированные поля класса
     * @param object целевой объект
     * @param annotation класс анотации Annotation.class
     * @return все поля находящиеся под annotation
     */
    public static AccessibleField[] getAnnotatedFields(Object object, Class<? extends Annotation> annotation) {
        Class<?> klass = object.getClass();
        List<Field> dtfFields = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                dtfFields.add(field);
            }
        }
        AccessibleField[] fields = new AccessibleField[dtfFields.size()];
        for (int i = 0, length = fields.length; i < length; i++) {
            fields[i] = new AccessibleField(dtfFields.get(i));
        }
        return fields;
    }

    /**
     * Конвертирует класс в JsonObject включая в него все публичные поля
     * @param object целевой объект
     * @return JsonObject
     */
    public static JsonObject toJsonObject(Object object) {
        return toJsonObject(object, null);
    }

    /**
     * Конвертирует класс в JsonObject включая в него все публичные поля и поля под аннотацией
     * @param object целевой объект
     * @param annotation аннотация
     * @return JsonObject
     */
    public static JsonObject toJsonObject(Object object, Class<? extends Annotation> annotation) {
        Field[] fields = getDeclaredFields(object);
        JsonObject jsonObject = new JsonObject();
        for (Field field : fields) {
            if ((field.canAccess(object) || (annotation != null && field.isAnnotationPresent(annotation)))) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value != null)
                        jsonObject.put(field.getName(), value);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    //сюда попасть не должен.
                    throw new RuntimeException(e);
                }
            }
        }
        return jsonObject;
    }

    public static <T> T createFromJson(String json, Class<T> klass) {
        return createFromJson(new JsonObject(json), klass);
    }

    //TODO: сделать поддержку не примитивных типов и массивов
    /**
     * Собирает объект класса type из JsonObject
     * @param jsonObject jsonObject
     * @param clazz экземпляр класса Class<T>
     * @param <T> автоматически определяемый параметр
     * @throws NullPointerException - в случае если конструктор clazz выбрасывает NullPointerException
     * @return возвращает класс который ожидается на выходе
     */
    public static <T> T createFromJson(JsonObject jsonObject, Class<T> clazz) {
        T result;
        try {
            result = newInstance(clazz);
        } catch (NullPointerException e) {
            throw new NullPointerException();
        } catch (Exception e) {
            //Никогда сюда не попадёт
            throw new RuntimeException(e);
        }
        Field[] fields = getDeclaredFields(result);
        for (Field field : fields) {
            Object fieldValue = null;
            if (field.isAnnotationPresent(TableField.class)) {
                fieldValue = jsonObject.getValue(field.getAnnotation(TableField.class).value());
            }
            if (fieldValue == null) {
                fieldValue = jsonObject.getValue(field.getName());
            }

            if (fieldValue != null)
                try {
                    field.setAccessible(true);
                    field.set(result, fieldValue);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    //Никогда сюда не попадёт.
                    throw new RuntimeException(e);
                }
        }
        return result;
    }

    //TODO: сделать поддержку не примитивных типов и массивов
    /**
     * Собирает объект класса type из JsonObject учитывая с конструктором который не может принимать null
     * @param jsonObject jsonObject
     * @param <T> автоматически определяемый параметр
     * @return возвращает класс который ожидается на выходе
     */
    public static <T> T createFromJson(JsonObject jsonObject, T clazz) {
        Field[] fields = getDeclaredFields(clazz);
        for (Field field : fields) {
            Object fieldValue = null;
            if (field.isAnnotationPresent(TableField.class)) {
                fieldValue = jsonObject.getValue(field.getAnnotation(TableField.class).value());
            }
            if (fieldValue == null) {
                fieldValue = jsonObject.getValue(field.getName());
            }
            if (fieldValue != null)
                try {
                    field.setAccessible(true);
                    field.set(clazz, fieldValue);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    //Никогда сюда не попадёт
                    throw new RuntimeException(e);
                }
        }
        return clazz;
    }

    /**
     * Возвращает экземпляр примитива или объекта
     * @param clazz Тип возвращаемого объекта
     * @param <T> Тип каста
     * @return Экземпляр типа T
     * @throws NullPointerException в случае если конструктор класса его выбрасывает
     */
    public static <T> T newInstance(Class<T> clazz) throws NullPointerException {
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Object[] parameters = new Object[constructor.getParameterCount()];
        Class<?>[] types = constructor.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            switch (types[i].getName()) {
                case "int": parameters[i] = 0; break;
                case "double": parameters[i] = 0d; break;
                case "float": parameters[i] = 0f; break;
                case "char": parameters[i] = (char)0; break;
                case "boolean": parameters[i] = false; break;
            }
        }
        try {
            Object result = constructor.newInstance(parameters);
            constructor.setAccessible(false);
            return (T) result;
        } catch (NullPointerException e) {
            throw new NullPointerException(e.toString());
        } catch (Exception e) {
            //Код никогда сюда не попадёт
            throw new RuntimeException(e);
        }
    }

    //TODO: данная функция никак не связана с reflection её нужно выделить в другой класс
    /**
     * Создаёт строку из массива с определённым разделителем
     * @param objects массив объектов которые нужно отделить символами из text
     * @param transformFunc функция получения String из objects
     * @param text символы разделяющие элементы массива
     * @return StringBuilder с текстом transformFunc.apply(objects[0]) + text + transformFunc.apply(objects[1]) ...
     */
    public static <T> StringBuilder arrayToSeparatedStringBuilder(Collection<T> objects, Function<T, String> transformFunc, String text) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        Integer integer;
        for (T field : objects) {
            if (!isFirst) {
                builder.append(text);
            }
            isFirst = false;
            builder.append(transformFunc.apply(field));
        }
        return builder;
    }
    //TODO: данная функция никак не связана с reflection её нужно выделить в другой класс
    public static <T> StringBuilder arrayToSeparatedStringBuilder(T[] objects, Function<T, String> transformFunc, String text) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        Integer integer;
        for (T field : objects) {
            if (!isFirst) {
                builder.append(text);
            }
            isFirst = false;
            builder.append(transformFunc.apply(field));
        }
        return builder;
    }

    public static Object Invoke(String methodName, String args, Object object) throws MethodNotFoundException {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Parameter[] parameters = method.getParameters();
                Object[] objectsArgs = new Object[parameters.length];
                for (int i = 0, length = objectsArgs.length; i < length; i++) {
                    objectsArgs[i] = createFromJson(args, parameters[i].getType());
                }
                try {
                    return method.invoke(object, objectsArgs);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new MethodNotFoundException();
    }

    public static Object Invoke(String methodName, JsonObject args, Object object) {
        return Invoke(methodName, args, object);
    }
}
