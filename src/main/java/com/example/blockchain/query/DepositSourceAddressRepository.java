package com.example.blockchain.query;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true", matchIfMissing = false)
public interface DepositSourceAddressRepository extends JpaRepository<DepositSourceAddress, UUID> {
    List<DepositSourceAddress> findAllByDepositId(UUID depositId);
} 