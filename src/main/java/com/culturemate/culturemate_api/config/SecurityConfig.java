package com.culturemate.culturemate_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CsrfTokenRepository csrfTokenRepository() {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    토큰 확인 후 post혹은 ajax의 검사하여 토큰이 없으면 전송 안시킴
//    http.csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
//      .ignoringRequestMatchers("/login")
//    )
    http.csrf((csrf) -> csrf.disable());
    http.authorizeHttpRequests((authorize) ->
      authorize.requestMatchers("/register", "/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
        .anyRequest().authenticated() // 로그인없이 접근할 수 있음
    );
    http.formLogin(formLogin -> formLogin
      .loginPage("/login")       // 로그인 페이지 지정
      .usernameParameter("loginId")  // 아이디 파라미터 이름 변경
      .passwordParameter("password")
      .defaultSuccessUrl("/chat/room/enter/1", true)  // 로그인 성공 시 /my-page로 이동
      .failureUrl("/login?error")   // 실패 시 리다이렉트
    );
    http.logout( logout -> logout.logoutUrl("/logout") );

    return http.build();
  }
}