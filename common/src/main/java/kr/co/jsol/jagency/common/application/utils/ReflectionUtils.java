package kr.co.jsol.jagency.common.application.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public class ReflectionUtils {

    public static Field getDeclaredField(Class<?> clazz, String name, Supplier<String> lazyMessage) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Object message = lazyMessage.get();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message + " " + name);
        }
    }
}
