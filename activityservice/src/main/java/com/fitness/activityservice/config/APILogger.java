package com.fitness.activityservice.configs;

import com.arthenica.smartexception.java9.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.UUID;

@Slf4j
public class APILogger {
    static {
        Exceptions.registerRootPackage("com.fitness.activityservice");
        Exceptions.registerIgnorePackage("jdk.internal", true);
        Exceptions.registerIgnorePackage("org.junit", true);
    }
    private JSONObject logger = new JSONObject();

    public APILogger(String httpMethod, String path, String requestId) {
        loadClassDetails();
        logger.put("method", httpMethod);
        logger.put("path", path);
        logger.put("requestId", requestId);
    }

    public APILogger(String method, String path) {
        this(method, path, UUID.randomUUID().toString());
    }

    private final void loadClassDetails() {
        Throwable throwable = new Throwable();
        StackTraceElement[] ste = throwable.getStackTrace();
        String klass = null;
        String function = null;
        for (StackTraceElement st: ste) {
            if (!st.getClassName().equals(this.getClass().getName())) {
                klass = st.getClassName();
                function = st.getMethodName();
                break;
            }
        }
        if (klass != null && function != null) {
            logger.put("klass", klass);
            logger.put("function", function);
        }
    }

    public void add(String key, String value) {
        logger.put(key, value);
        log.info(key + " " + value);
    }

    public void logSuccess(int statusCode) {
        logger.put("reason", "success");
        logger.put("status", String.valueOf(statusCode));
        log.info(logger.toString());
    }

    public void logError(String error, int statusCode, Throwable e) {
        logger.put("reason", error);
        logger.put("status", String.valueOf(statusCode));
        if (e != null) {
            logger.put("exception", Exceptions.getStackTraceString(e, 5));
        }
        log.error(logger.toString());
    }

    public void logError(String error, int statusCode) {
        logError(error, statusCode, null);
    }

    public void addWithHash(String key, String value) {
        logger.put(key, getHash(value));
    }

    private String getHash(String stringToHash) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(stringToHash.getBytes());
            String hashedStr  = new String(messageDigest.digest());
            return Base64.encodeBase64String(hashedStr.getBytes());
        } catch (Exception e) {
            return "Hash_Failed";
        }
    }
}
