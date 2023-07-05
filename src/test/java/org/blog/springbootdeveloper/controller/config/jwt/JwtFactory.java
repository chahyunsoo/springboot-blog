package org.blog.springbootdeveloper.controller.config.jwt;


import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Getter;
import org.blog.springbootdeveloper.config.jwt.JwtProperties;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Getter
public class JwtFactory {
    private String subject = "test@email.com";
    private Date issudeAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = emptyMap();

    //Builder 패턴을 사용하여 설정이 필요한 데이터만 선택 설정함
    @Builder
    public JwtFactory(String subject,Date issudeAt,Date expiration,
                      Map<String,Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issudeAt = issudeAt != null ? issudeAt : this.issudeAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValues() {
        return JwtFactory.builder().build();
    }

    //jjwt 라이브러리를 사용해서 JWT 토큰을 생성함
    public String createToken(JwtProperties jwtProperties) {
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issudeAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }
}
