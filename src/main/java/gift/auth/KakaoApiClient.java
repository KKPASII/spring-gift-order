package gift.auth;

import gift.auth.dto.KakaoTokenCreationRequest;
import gift.auth.dto.KakaoTokenResponse;
import gift.auth.dto.KakaoUserInfoResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static gift.global.config.AuthConstants.BEARER_PREFIX;

@Component
public class KakaoApiClient {
    private static final String TOKEN_CREATION_PATH = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_PATH = "https://kapi.kakao.com/v2/user/me";

    private final RestClient kakaoRestClient;

    public KakaoApiClient(RestClient kakaoRestClient) {
        this.kakaoRestClient = kakaoRestClient;
    }

    public KakaoTokenResponse createToken(KakaoTokenCreationRequest request) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", request.grantType());
        requestBody.add("client_id", request.clientId());
        requestBody.add("redirect_uri", request.redirectUri());
        requestBody.add("code", request.code());

        return kakaoRestClient.post()
            .uri(TOKEN_CREATION_PATH)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(requestBody)
            .retrieve()
            .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return kakaoRestClient.get()
            .uri(USER_INFO_PATH)
            .header("Authorization", BEARER_PREFIX + accessToken)
            .retrieve()
            .body(KakaoUserInfoResponse.class);
    }
}