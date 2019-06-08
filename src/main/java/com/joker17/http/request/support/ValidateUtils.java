package com.joker17.http.request.support;

public class ValidateUtils {

    public static void checkKeyNameNotEmpty(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("key must be not empty!");
        }
    }

}
