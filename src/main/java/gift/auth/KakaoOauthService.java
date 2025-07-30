package gift.auth;

import gift.auth.dto.KakaoProperties;
import gift.auth.dto.KakaoTokenCreationRequest;
import gift.auth.dto.KakaoTokenResponse;
import gift.auth.dto.KakaoUserInfoResponse;
import gift.member.entity.Member;
import gift.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoOauthService {
    private static final String AUTHORIZATION_PATH = "https://kauth.kakao.com/oauth/authorize";

    private final KakaoApiClient kakaoApiClient;
    private final KakaoProperties kakaoProperties;
    private final MemberService memberService;

    KakaoOauthService(KakaoApiClient kakaoApiClient, KakaoProperties kakaoProperties, MemberService memberService) {
        this.kakaoApiClient = kakaoApiClient;
        this.kakaoProperties = kakaoProperties;
        this.memberService = memberService;
    }

    public String getKakaoLoginUrl() {
        return UriComponentsBuilder
            .fromUriString(AUTHORIZATION_PATH)
            .queryParam("response_type", "code")
            .queryParam("client_id", kakaoProperties.clientId())
            .queryParam("redirect_uri", kakaoProperties.redirectUri())
            .queryParam("scope", "talk_message")
            .toUriString();
    }

    /**
     * 인가 코드를 받아 카카오 로그인을 처리하고, 우리 서비스의 회원 정보를 반환합니다.
     * @param code 카카오로부터 받은 인가 코드
     * @return Member 우리 서비스의 회원 엔티티
     */
    @Transactional
    public Member login(String code) {
        KakaoTokenCreationRequest tokenRequest = new KakaoTokenCreationRequest(
            "authorization_code",
            kakaoProperties.clientId(),
            kakaoProperties.redirectUri(),
            code
        );
        KakaoTokenResponse tokenResponse = kakaoApiClient.createToken(tokenRequest);

        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(tokenResponse.accessToken());
        Long kakaoUserId = userInfo.id();

        return memberService.findOrCreateMemberByKakaoId(kakaoUserId, tokenResponse.accessToken());
    }
}
