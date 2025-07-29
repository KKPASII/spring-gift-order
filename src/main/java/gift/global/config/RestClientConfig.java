package gift.global.config;

import gift.auth.dto.KakaoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient kakaoRestClient() {
        return RestClient.builder().build();
    }
}
