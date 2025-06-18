package com.nsmm.esg.csddd_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret}")
    private String secret;

    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("JWT Secret Key Length: {}", secret.length());
        log.info("JWT Secret Key (first 10 chars): {}", secret.substring(0, Math.min(10, secret.length())));

        try {
            // Auth 서비스와 정확히 동일한 방식으로 SecretKey 생성
            // Keys.hmacShaKeyFor()와 동일한 방식 사용
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA512");

            // 키 길이 확인 (HS512는 최소 512비트/64바이트 필요)
            log.info("Secret key length in bytes: {}", secret.getBytes().length);

            // 명시적으로 HS512 알고리즘 지정
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                    .build();

            log.info("JWT Decoder configured with HS512 algorithm");
            return decoder;

        } catch (Exception e) {
            log.error("Failed to create JWT Decoder", e);
            throw e;
        }
    }
}