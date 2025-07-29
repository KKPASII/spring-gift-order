package gift.auth.controller;

import gift.auth.dto.AuthRequest;
import gift.auth.dto.AuthToken;
import gift.auth.dto.KakaoTokenResponse;
import gift.auth.dto.KakaoUserInfoResponse;
import gift.global.util.JwtUtil;
import gift.global.util.TokenCookieUtils;
import gift.member.entity.Member;
import gift.member.entity.Role;
import gift.member.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static gift.global.config.AuthConstants.BEARER_PREFIX;

@Controller
public class LoginController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final RestClient restClient;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    private final String kakaoUserInfoUri = "https://kapi.kakao.com/v2/user/me";

    public LoginController(MemberService memberService, JwtUtil jwtUtil, RestClient restClient) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.restClient = restClient;
    }

    @GetMapping("/")
    public String kakaoCallback(
        @RequestParam("code") String code,
        HttpServletResponse response
    ) throws UnsupportedEncodingException {
        // 카카오 서버에 액세스 토큰 요청
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", kakaoClientId);
        requestBody.add("redirect_uri", kakaoRedirectUri);
        requestBody.add("code", code);

        // 카카오 서버에서 액세스 토큰 받기
        KakaoTokenResponse kakaoTokenResponse = restClient.post()
            .uri(kakaoTokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(requestBody)
            .retrieve()
            .body(KakaoTokenResponse.class);

        if (kakaoTokenResponse == null) {
            return "redirect:/login";
        }

        response.addCookie(TokenCookieUtils.createAccessTokenCookie(kakaoTokenResponse.accessToken()));

        KakaoUserInfoResponse userInfo = restClient.get()
            .uri(kakaoUserInfoUri)
            .header("Authorization", BEARER_PREFIX + kakaoTokenResponse.accessToken())
            .retrieve()
            .body(KakaoUserInfoResponse.class);

        if (userInfo == null) {
            return "redirect:/login";
        }

        response.addCookie(TokenCookieUtils.createAccessTokenCookie(kakaoTokenResponse.accessToken()));

        return "redirect:login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        String scope = "talk_message";
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri + "&scope=" + scope;
        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> processLogin(@Valid @RequestBody AuthRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        AuthToken token = memberService.login(request);
        String accessToken = token.accessToken();

        Claims claims = jwtUtil.getClaims(accessToken);
        Role role = Role.valueOf(claims.get("role", String.class));
        response.addCookie(TokenCookieUtils.createAccessTokenCookie(accessToken));

        String redirectUrl = "/";
        if (role == Role.ADMIN) {
            redirectUrl = "/management/products";
        } else if (role == Role.USER) {
            redirectUrl = "/wishes";
        }
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }
}