package com.example.blockchain.query;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true", matchIfMissing = false)
public interface DepositRepository extends JpaRepository<Deposit, UUID> {
    List<Deposit> findAllBySrcSyncedFalse();
    Optional<Deposit> findByTxHash(String txHash);
    Page<Deposit> findAllBySrcSyncedFalse(Pageable pageable);
} 