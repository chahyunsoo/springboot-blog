package org.blog.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.dto.CreateAccessTokenRequest;
import org.blog.springbootdeveloper.dto.CreateAccessTokenResponse;
import org.blog.springbootdeveloper.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken()); //리프레시 토큰으로 새로운 액세스 토큰의 값 만듬

        return ResponseEntity.status(HttpStatus.CREATED)  //새로운 리소스가 성공적으로 생성되었음
                .body(new CreateAccessTokenResponse(newAccessToken)); //HTTP응답 본문에 담길 데이터, 액세서 토큰 값 설정, 클라이언트는 새로운 액세스 토큰 값을 확인 가능함
    }
}
