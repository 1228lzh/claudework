package com.cluehub.service;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护 access_token → HttpSession 的映射，用于飞连 backchannel 单点登出
 */
@Component
public class SessionRegistry {

    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    public void register(String accessToken, HttpSession session) {
        sessions.put(accessToken, session);
    }

    public void removeByToken(String accessToken) {
        HttpSession session = sessions.remove(accessToken);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
                // session already invalidated
            }
        }
    }

    public void remove(HttpSession session) {
        sessions.values().removeIf(s -> s == session);
    }
}
