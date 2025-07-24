package gift.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
    @JsonProperty("id") Long id
) {
}
