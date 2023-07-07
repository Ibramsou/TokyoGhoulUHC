package fr.hysekai.tokyo.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflection {

    private static final Field modifiersField = accessField(Field.class, "modifiers");
    private static final int version;

    static {
        final String ver = System.getProperty("java.version");
        final String[] parts = ver.split("\\.");
        version = Integer.parseInt(parts[0].equals("1") ? parts[1] : parts[0]);
    }

    public static Field accessField(String className, String field) {
        try {
            return accessField(Class.forName(className), field);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find class named '" + className + "'", e);
        }
    }

    public static Field accessField(Class<?> clazz, String field) {
        try {
            Field result = clazz.getDeclaredField(field);
            result.setAccessible(true);
            return result;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Cannot access to field named '" + field + "' from " + clazz.getName(), e);
        }
    }

    public static Method accessMethod(String className, String method, Class<?>... types) {
        try {
            return accessMethod(Class.forName(className), method, types);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find class named '" + className + "'", e);
        }
    }

    public static Method accessMethod(Class<?> clazz, String method, Class<?>... types) {
        try {
            Method result = clazz.getDeclaredMethod(method, types);
            result.setAccessible(true);
            return result;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Cannot access to method named '" + method + "' from " + clazz.getName(), e);
        }
    }

    public static void set(Object instance, Field field, Object value, boolean modifiers) {
        try {
            if (modifiers && version < 9) {
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot set field named '" + field.getName() + "' from " + instance.getClass().getName(), e);
        }
    }

    public static void set(Object instance, Field field, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot set field named '" + field.getName() + "' from " + instance.getClass().getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V invoke(Object instance, Method method, Object... parameters) {
        try {
            return (V) method.invoke(instance, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot invoke method named '" + method.getName() + "' from " + instance.getClass().getName(), e);
        }
    }
}
