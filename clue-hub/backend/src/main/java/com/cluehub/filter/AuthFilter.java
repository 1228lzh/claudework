package com.cluehub.filter;

import com.cluehub.model.UserInfo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(1)
public class AuthFilter implements Filter {

    private static final String SESSION_USER_KEY = "currentUser";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI();

        if (path.startsWith("/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            UserInfo user = (UserInfo) session.getAttribute(SESSION_USER_KEY);
            if (user != null) {
                request.setAttribute("currentUser", user);
                chain.doFilter(req, res);
                return;
            }
        }

        response.sendRedirect("/auth/login");
    }
}
