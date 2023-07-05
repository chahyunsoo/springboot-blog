package org.blog.springbootdeveloper.controller.config.jwt;

import io.jsonwebtoken.Jwts;
import org.blog.springbootdeveloper.config.jwt.JwtProperties;
import org.blog.springbootdeveloper.config.jwt.TokenProvider;
import org.blog.springbootdeveloper.domain.User;
import org.blog.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    //generateToken() 검증 테스트
    @DisplayName("generateToken(): User 정보와 만료 기간을 전달해 토큰을 만들 수 있다")
    @Test
    void generateToken() {
        //given
        User user1 = userRepository.save(User.builder()
                .email("coys@gmail.com")
                .password("coys")
                .build());

        //when
        String token = tokenProvider.generateToken(user1, Duration.ofDays(14));

        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class); //claim에서 id속성을 Long형식으로 가져오셈

        assertThat(userId).isEqualTo(user1.getId());
    }

    //validToken() 검증 테스트
    @DisplayName("validToken(): 만료된 토큰이면 유휴성 검증에 실패한다")
    @Test
    void validToken_invalid() {
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build() //여기까지가 JwtFactory의 객체를 생성
                .createToken(jwtProperties);  //Jwt 토큰 생성

        //when
        boolean testResult = tokenProvider.validToken(token);

        //then
        assertThat(testResult).isFalse();
    }

    @DisplayName("validToken(): 유효한 토큰이면 유휴성 검증에 성공한다")
    @Test
    void validToken_valid() {
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        //when
        boolean testResult = tokenProvider.validToken(token);

        //then
        assertThat(testResult).isTrue();
    }


    //getAuthentication() 검증 테스트(토큰을 사용해서 인증 정보를 추출할 수 있는지를 검증함)
    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다")
    @Test
    void getAuthentication() {
        //given
        String userEmail = "spurs@naver.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        //when
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
        //getPrincipal(): Authentication 객체에 저장된 주제 정보를 가져옴
    }

    //getUserId() 검증 테스트
    @DisplayName("getUserId(): 토큰으로 User Id를 가져올 수 있다")
    @Test
    void getUserId() {
        //given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        //when
        Long userIdByToken = tokenProvider.getUserId(token);

        //given
        assertThat(userIdByToken).isEqualTo(userId);
    }


}

