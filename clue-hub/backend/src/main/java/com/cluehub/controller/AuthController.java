package com.cluehub.controller;

import com.cluehub.config.FeilianProperties;
import com.cluehub.model.UserInfo;
import com.cluehub.service.SessionRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final FeilianProperties feilian;
    private final SessionRegistry sessionRegistry;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SESSION_USER_KEY = "currentUser";
    private static final String SESSION_STATE_KEY = "oauth2State";

    @GetMapping("/login")
    public void login(HttpSession session, HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        session.setAttribute(SESSION_STATE_KEY, state);
        String url = feilian.getBaseUri() + "/oauth2/authorize"
                + "?client_id=" + feilian.getClientId()
                + "&redirect_uri=" + feilian.getRedirectUri()
                + "&response_type=code"
                + "&state=" + state;
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code, @RequestParam String state,
                         HttpSession session, HttpServletResponse response) throws IOException {
        String savedState = (String) session.getAttribute(SESSION_STATE_KEY);
        if (savedState == null || !savedState.equals(state)) {
            log.warn("State mismatch: saved={}, received={}", savedState, state);
            response.sendRedirect("/auth/login");
            return;
        }
        session.removeAttribute(SESSION_STATE_KEY);
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", feilian.getClientId());
            body.add("client_secret", feilian.getClientSecret());
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", feilian.getRedirectUri());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> tokenResp = restTemplate.postForEntity(
                    feilian.getBaseUri() + "/oauth2/token",
                    new HttpEntity<>(body, headers), String.class);
            ObjectMapper mapper = new ObjectMapper();
            String tokenBody = tokenResp.getBody();
            log.info("Token response: {}", tokenBody);
            JsonNode tokenJson = mapper.readTree(tokenBody);
            String accessToken = tokenJson.get("access_token").asText();
            log.info("Got access_token: {}", accessToken.substring(0, Math.min(8, accessToken.length())) + "...");
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            ResponseEntity<String> userResp = restTemplate.exchange(
                    feilian.getBaseUri() + "/oauth2/userinfo/normal",
                    HttpMethod.GET, new HttpEntity<>(userHeaders), String.class);
            String userBody = userResp.getBody();
            log.info("Userinfo response: {}", userBody);
            JsonNode userJson = mapper.readTree(userBody);
            UserInfo user = new UserInfo();
            user.setUserId(userJson.get("user_id").asText());
            user.setFullname(userJson.has("fullname") ? userJson.get("fullname").asText() : "");
            user.setMobile(userJson.has("mobile") ? userJson.get("mobile").asText() : "");
            user.setEmail(userJson.has("email") ? userJson.get("email").asText() : "");
            user.setAdmin(isAdminUser(user.getUserId()));
            session.setAttribute(SESSION_USER_KEY, user);
            sessionRegistry.register(accessToken, session);
            response.sendRedirect("/");
        } catch (Exception e) {
            log.error("Feilian OAuth2 callback failed", e);
            response.sendRedirect("/auth/error?reason=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    @GetMapping("/error")
    public void error(@RequestParam(defaultValue = "unknown") String reason, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>登录失败</title>");
        out.println("<style>body{font-family:sans-serif;padding:40px;text-align:center}</style></head><body>");
        out.println("<h2>登录失败</h2>");
        out.println("<p>原因: " + reason + "</p>");
        out.println("<p><a href='/auth/login'>重新登录</a></p>");
        out.println("</body></html>");
    }

    @GetMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        sessionRegistry.remove(session);
        session.invalidate();
        String url = feilian.getBaseUri() + "/oauth2/logout"
                + "?post_logout_redirect_uri=" + feilian.getPostLogoutRedirectUri();
        response.sendRedirect(url);
    }

    @PostMapping("/backchannel_logout")
    public ResponseEntity<?> backchannelLogout(@RequestParam("logout_token") String logoutToken) {
        log.info("Backchannel logout received");
        sessionRegistry.removeByToken(logoutToken);
        return ResponseEntity.ok("{}");
    }

    @GetMapping("/user")
    public ResponseEntity<?> user(HttpSession session) {
        UserInfo user = (UserInfo) session.getAttribute(SESSION_USER_KEY);
        if (user == null) {
            return ResponseEntity.status(401).body("{\"code\":401,\"message\":\"未登录\"}");
        }
        return ResponseEntity.ok(user);
    }

    private boolean isAdminUser(String userId) {
        String adminIds = feilian.getAdminIds();
        if (adminIds == null || adminIds.isEmpty()) return false;
        for (String id : adminIds.split(",")) {
            if (id.trim().equals(userId)) return true;
        }
        return false;
    }
}
