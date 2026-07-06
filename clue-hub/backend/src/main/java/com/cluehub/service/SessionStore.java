package com.cluehub.service;

import com.cluehub.model.UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按 sessionId 存储用户，绕过 Cookie SameSite 限制
 */
@Component
public class SessionStore {

    private final Map<String, UserInfo> store = new ConcurrentHashMap<>();

    public void put(String sessionId, UserInfo user) {
        store.put(sessionId, user);
    }

    public UserInfo get(String sessionId) {
        return store.get(sessionId);
    }

    public void remove(String sessionId) {
        store.remove(sessionId);
    }
}
