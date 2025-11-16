package com.eric.taskflow.security;


import com.eric.taskflow.repository.TokenBlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BlackListCleaner {

    private final TokenBlackListRepository blacklistRepository;

    @Scheduled(fixedRate = 15 * 60 * 1000) // Cada 15 minutos
    public void cleanExpiredTokens() {
        blacklistRepository.deleteExpired(Instant.now());
    }

}
