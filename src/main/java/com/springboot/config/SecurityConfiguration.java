package com.springboot.config;

import com.springboot.auth.MemberDetailsService;
import com.springboot.auth.filter.JwtAuthenticationFilter;
import com.springboot.auth.filter.JwtVerificationFilter;
import com.springboot.auth.handler.MemberAccessDeniedHandler;
import com.springboot.auth.handler.MemberAuthenticationEntryPoint;
import com.springboot.auth.handler.MemberAuthenticationFailureHandler;
import com.springboot.auth.handler.MemberAuthenticationSuccessHandler;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.AuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메서드 보안 활성화
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final AuthorityUtils authorityUtils;
    private final MemberDetailsService memberDetailsService;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, AuthorityUtils authorityUtils, MemberDetailsService memberDetailsService) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.memberDetailsService = memberDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(Customizer.withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        // Member
                        .antMatchers(HttpMethod.GET, "/qna/members/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/qna/members").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/qna/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE, "/qna/members/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/qna/members").permitAll()
                        // Question
                        .antMatchers(HttpMethod.POST, "/qna/questions").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/qna/questions/**").authenticated()
                        .antMatchers(HttpMethod.GET, "/qna/questions/**").authenticated()
                        .antMatchers(HttpMethod.GET, "/qna/questions").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/qna/questions/**").hasRole("USER")
                        // Answer
                        .antMatchers("/qna/questions/**/answers").hasRole("ADMIN")
                        .antMatchers("/qna/questions/**/answers/**").hasRole("ADMIN")
                        // Like
                        .antMatchers("/qna/questions/**/like").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // CorsConfigurationSource Bean 생성을 통해 구체적인 CORS 정책 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // setAllowedOrigins : 모든 출처에 대해 허용
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // setAllowedMethods : 지정한 HTTP Method 에 대한 통신 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));

        // UrlBasedCorsConfigurationSource : CorsConfigurationSource의 구현체
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 URL에 CORS 정책 적용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 구현한 JwtAuthenticationFilter 를 등록하는 역할
    // AbstractHttpConfigurer 를 상속
    // AbstractHttpConfigurer를 상속하는 타입과 HttpSecurityBuilder를 상속하는 타입을 제너릭 타입으로 지정할 수 있음
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        // configure() 메서드를 오버라이드해서 Configuration을 커스터마이징
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            // getSharedObject() : Spring Security의 설정을 구성하는 SecurityConfigurer 간에 공유되는 객체 가져올 수 있음
            // AuthenticationManager 의 객체 가져옴
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            // JwtAuthenticationFilter를 생성하면서
            // JwtAuthenticationFilter에서 사용되는 AuthenticationManager와 JwtTokenizer를 DI
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);
            // setFilterProcessesUrl() 메서드를 통해 디폴트 request URL인 “/login”을 “/v11/auth/login”으로 변경
            jwtAuthenticationFilter.setFilterProcessesUrl("/qna/auth/login");

            // success handler 등록
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            // failure handler 등록
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            //  JwtVerificationFilter의 인스턴스를 생성 + JwtVerificationFilter에서 사용되는 객체들을 생성자로 DI
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils, memberDetailsService);

            // addFilter() 메서드를 통해 JwtAuthenticationFilter를 Spring Security Filter Chain에 추가
            builder.addFilter(jwtAuthenticationFilter)
                    // JwtVerificationFilter를 JwtAuthenticationFilter 뒤에 추가
                    // JwtAuthenticationFilter에서 로그인 인증에 성공한 후 발급받은 JWT가
                    // 클라이언트의 request header(Authorization 헤더)에 포함되어 있을 경우에만 동작
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}
