package gift.auth.controller;

import gift.auth.KakaoOauthService;
import gift.auth.dto.AuthRequest;
import gift.auth.dto.AuthToken;
import gift.global.util.JwtUtil;
import gift.global.util.TokenCookieUtils;
import gift.member.entity.Member;
import gift.member.entity.Role;
import gift.member.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
public class LoginController {
    private final KakaoOauthService kakaoOauthService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public LoginController(MemberService memberService, JwtUtil jwtUtil, KakaoOauthService kakaoOauthService) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.kakaoOauthService = kakaoOauthService;
    }

    @GetMapping("/")
    public String kakaoCallback(
        @RequestParam("code") String code,
        HttpServletResponse response
    ) throws UnsupportedEncodingException {
        Member member = kakaoOauthService.login(code);
        String appToken = jwtUtil.generateAccessToken(member);
        response.addCookie(TokenCookieUtils.createAccessTokenCookie(appToken));
        return "redirect:/wishes";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("kakaoLoginUrl", kakaoOauthService.getKakaoLoginUrl());
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