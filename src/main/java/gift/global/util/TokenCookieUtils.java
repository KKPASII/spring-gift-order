package gift.global.util;

import jakarta.servlet.http.Cookie;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static gift.global.config.AuthConstants.BEARER_PREFIX;

public final class TokenCookieUtils {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final int DEFAULT_MAX_AGE_SECONDS = 60 * 30;

    private TokenCookieUtils() {
    }

    public static Cookie createAccessTokenCookie(String accessToken) {
        String cookieValue = URLEncoder.encode(BEARER_PREFIX + accessToken, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(DEFAULT_MAX_AGE_SECONDS);
        return cookie;
    }
}