package com.eric.taskflow.security;


import com.eric.taskflow.repository.TokenBlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final TokenBlackListRepository blacklistRepository;

    public void blacklist(String jti, Instant expiresAt) {
        TokenBlackList token = TokenBlackList.builder()
                .jti(jti)
                .expiresAt(expiresAt)
                .build();

        blacklistRepository.save(token);
    }

    public boolean isBlacklisted(String jti) {
        return blacklistRepository.existsByJti(jti);
    }

}
