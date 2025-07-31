package com.example.blockchain.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true", matchIfMissing = false)
public class DepositService {

    private final DepositRepository depositRepository;
    private final DepositSourceAddressRepository depositSourceAddressRepository;

    public DepositService(DepositRepository depositRepository,
                         DepositSourceAddressRepository depositSourceAddressRepository) {
        this.depositRepository = depositRepository;
        this.depositSourceAddressRepository = depositSourceAddressRepository;
    }

    public DepositDetailsDto getDepositByTxHash(String txHash) {
        log.info("Looking up deposit by txHash: {}", txHash);
        
        Optional<Deposit> depositOpt = depositRepository.findByTxHash(txHash);
        if (depositOpt.isEmpty()) {
            log.warn("Deposit not found for txHash: {}", txHash);
            return null;
        }
        
        Deposit deposit = depositOpt.get();
        List<DepositSourceAddress> sourceAddresses = depositSourceAddressRepository.findAllByDepositId(deposit.getId());
        log.info("Found {} source addresses for deposit {}", sourceAddresses.size(), deposit.getId());
        
        return new DepositDetailsDto(deposit, sourceAddresses);
    }
} 