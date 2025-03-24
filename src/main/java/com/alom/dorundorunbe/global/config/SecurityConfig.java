package com.alom.dorundorunbe.global.config;

import com.alom.dorundorunbe.domain.auth.handler.OAuthFailureHandler;
import com.alom.dorundorunbe.domain.auth.handler.OAuthSuccessHandler;
import com.alom.dorundorunbe.domain.auth.provider.JwtTokenProvider;
import com.alom.dorundorunbe.domain.auth.service.PrincipalUserDetailsService;
import com.alom.dorundorunbe.global.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final PrincipalUserDetailsService principalUserDetailsService;
  private final OAuthSuccessHandler oAuthSuccessHandler;
  private final OAuthFailureHandler oAuthFailureHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)


        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/actuator/**",
                "/doodle/**",
                "/records/**"
            ).permitAll() // Swagger 및 관련 리소스 허용
            .anyRequest().authenticated()) // 나머지 요청은 인증 필요
        .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(principalUserDetailsService))
                .successHandler(oAuthSuccessHandler)
                .failureHandler(oAuthFailureHandler)) // oauth2

        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, principalUserDetailsService), UsernamePasswordAuthenticationFilter.class);

    http.cors(AbstractHttpConfigurer::disable); // 테스트를 위해 CORS 비활성화

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}