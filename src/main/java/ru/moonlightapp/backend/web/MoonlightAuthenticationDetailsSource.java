package ru.moonlightapp.backend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public final class MoonlightAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    public static final MoonlightAuthenticationDetailsSource SINGLETON = new MoonlightAuthenticationDetailsSource();

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        String remoteAddr = context.getRemoteAddr();
        String nginxRealIP = context.getHeader("X-Real-IP");

        if ("127.0.0.1".equals(remoteAddr) && nginxRealIP != null && !remoteAddr.equals(nginxRealIP)) {
            HttpSession session = context.getSession(false);
            String sessionId = session != null ? session.getId() : null;
            return new WebAuthenticationDetails(nginxRealIP, sessionId);
        } else {
            return new WebAuthenticationDetails(context);
        }
    }

}