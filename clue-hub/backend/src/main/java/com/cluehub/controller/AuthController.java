package com.cluehub.controller;

import com.cluehub.config.FeilianProperties;
import com.cluehub.model.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final FeilianProperties feilian;
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
            response.sendError(400, "Invalid state");
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
            JsonNode tokenJson = mapper.readTree(tokenResp.getBody());
            String accessToken = tokenJson.get("access_token").asText();
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            ResponseEntity<String> userResp = restTemplate.exchange(
                    feilian.getBaseUri() + "/oauth2/userinfo/normal",
                    HttpMethod.GET, new HttpEntity<>(userHeaders), String.class);
            JsonNode userJson = mapper.readTree(userResp.getBody());
            JsonNode data = userJson.get("data");
            UserInfo user = new UserInfo();
            user.setUserId(data.get("user_id").asText());
            user.setFullname(data.has("full_name") ? data.get("full_name").asText() : "");
            user.setMobile(data.has("mobile") ? data.get("mobile").asText() : "");
            user.setEmail(data.has("email") ? data.get("email").asText() : "");
            session.setAttribute(SESSION_USER_KEY, user);
            response.sendRedirect("/");
        } catch (Exception e) {
            response.sendRedirect("/auth/error");
        }
    }

    @GetMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        String url = feilian.getBaseUri() + "/oauth2/logout"
                + "?post_logout_redirect_uri=" + feilian.getPostLogoutRedirectUri();
        response.sendRedirect(url);
    }

    @GetMapping("/user")
    public ResponseEntity<?> user(HttpSession session) {
        UserInfo user = (UserInfo) session.getAttribute(SESSION_USER_KEY);
        if (user == null) {
            return ResponseEntity.status(401).body("{\"code\":401,\"message\":\"未登录\"}");
        }
        return ResponseEntity.ok(user);
    }
}
