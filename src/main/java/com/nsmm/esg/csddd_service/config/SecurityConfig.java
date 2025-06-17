package com.nsmm.esg.csddd_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security 설정 - 인증/인가 규칙 정의
     * 현재는 Postman 테스트 및 외부 API 접근을 위한 설정만 포함
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (Postman, REST API 테스트를 위해)
                .csrf(csrf -> csrf.disable())

                // HTTP Basic 인증 비활성화 (별도 인증 수단 사용 예정 시 필요)
                .httpBasic(httpBasic -> httpBasic.disable())

                // 세션 사용 안 함 - JWT 기반 인증 또는 Stateless API 구성에 적합
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청별 허용 정책 설정
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 메서드는 CORS 사전 요청이므로 항상 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ 자가진단 API 전체 허용 (모든 HTTP 메서드 포함)
                        .requestMatchers("/api/csddd/**").permitAll()

                        // Spring Actuator 엔드포인트 허용 (상태 확인용)
                        .requestMatchers("/actuator/**").permitAll()

                        // 그 외 요청은 전부 차단
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}