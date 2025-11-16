package com.eric.taskflow.repository;

import com.eric.taskflow.security.TokenBlackList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, Long> {
    boolean existsByJti(String jti);

    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlackList t WHERE t.expiresAt < :now")
    void deleteExpired(Instant now);
}
