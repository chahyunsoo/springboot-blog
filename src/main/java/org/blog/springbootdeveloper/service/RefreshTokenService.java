package org.blog.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import org.blog.springbootdeveloper.domain.RefreshToken;
import org.blog.springbootdeveloper.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
