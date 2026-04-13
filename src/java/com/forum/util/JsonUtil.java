package com.forum.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JsonUtil — thin wrapper around Gson.
 *
 * Centralises Gson configuration so every Servlet uses the same
 * date format and pretty-print settings.
 *
 * Module coverage: Module 3 — JSON data interchange
 */
public class JsonUtil {

    // Single shared Gson instance (Gson is thread-safe)
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();

    /** Serialise any object to a JSON string. */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /** Deserialise a JSON string into an object of the given type. */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    private JsonUtil() {}
}
