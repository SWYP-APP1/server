package com.swyp.futsal.config;

import com.google.firebase.auth.FirebaseAuth;
import com.swyp.futsal.domain.user.service.UserService;
import com.swyp.futsal.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final FirebaseAuth firebaseAuth;
  private final UserService userService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인폼 비활성화
        .cors(cors -> cors.configurationSource(configurationSource()))
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .addFilterBefore(new JwtFilter(userService, firebaseAuth),
            UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private CorsConfigurationSource configurationSource() {
    return request -> {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowCredentials(true);
      configuration.addAllowedOrigin("*");
      configuration.addAllowedHeader("*");
      configuration.addAllowedMethod("*");
      return configuration;
    };
  }


}
