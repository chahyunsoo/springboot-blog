package org.blog.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;
    /*
    @Autowired
    private final JwtProperties jwtProperties;   -->허용 안됨
    이유는, final은 선언과 동시에 초기화 or  생성자에서 초기화해야 함
    @Autowired는 Bean의 생명주기에 따라 필드에 값을 주입하는데,이것은 생성자 호출 이후에 발생하므로
    final에는 붙일 수 없다.
     */

//    @RequiredArgsConstructor가 대체
//    public TokenProvider(JwtProperties jwtProperties) {
//        this.jwtProperties = jwtProperties;
//    }

    //JWT 토큰 생성 메소드, 사용자 정보와 토큰의 만료시간을 받아 토큰 생성
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //JWT 토큰 생성 메소드, 실제 토큰을 만드는 로직, 토큰의 유효기간과 사용자 정보를 받음
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    //받은 토큰이 유효한지 검증하는 메소드
    public boolean validToken(String token) {
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) //서명 검증할 때 사용할 비밀키 설정
                    .parseClaimsJws(token);                      //실제 파싱 & 검증
            return true;  //아무 에러가 나지 않음
        }catch (Exception e){  //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    //JWT 토큰에서 인증정보를 추출
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.
                security.core.userdetails.User(claims.getSubject
                (),"",authorities), token, authorities);
    }

    //토큰에서 사용자 ID 추출
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    //토큰에서 Claims 객체 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();  //payload부분 반환함
    }
}
