package com.example.blockchain.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true", matchIfMissing = false)
public class DepositSyncService {

    private final DepositRepository depositRepository;
    private final DepositSourceAddressRepository sourceAddressRepository;
    private final TransactionSourceAddressProvider transactionSourceAddressProvider;

    private final RateLimiter rateLimiter = RateLimiter.create(5.0); // 200 requests per second

    public void syncDeposits() {
        log.info("Starting deposit synchronization...");
        
        int page = 0;
        int batchSize = 1000;
        boolean hasMore = true;
        
        while (hasMore) {
            Page<Deposit> depositsPage = depositRepository.findAllBySrcSyncedFalse(
                PageRequest.of(page, batchSize)
            );
            
            if (depositsPage.isEmpty()) {
                log.info("No more unsynced deposits found");
                break;
            }
            
            log.info("Processing batch {} with {} deposits", page, depositsPage.getContent().size());
            
            for (Deposit deposit : depositsPage.getContent()) {
                try {
                    processDeposit(deposit);
                } catch (Exception e) {
                    log.error("Error processing deposit {}: {}", deposit.getId(), e.getMessage(), e);
                }
            }
            
            page++;
            hasMore = depositsPage.hasNext();
        }
        
        log.info("Deposit synchronization completed");
    }

    private void processDeposit(Deposit deposit) {
        String tatumBlockchain = NetworkMapper.toTatumBlockchain(deposit.getNetwork());
        
        if ("UNSUPPORTED".equals(tatumBlockchain)) {
            log.warn("Unsupported network for deposit {}: {}", deposit.getId(), deposit.getNetwork());
            return;
        }

        // Apply rate limiting
        rateLimiter.acquire();

        try {
            JsonNode jsonResponse;
            List<String> sourceAddresses = transactionSourceAddressProvider.provide(tatumBlockchain, deposit.getTxHash());
            

            if (!sourceAddresses.isEmpty()) {
                // Save source addresses
                List<DepositSourceAddress> addressesToSave = new ArrayList<>();
                for (String address : sourceAddresses) {
                    DepositSourceAddress sourceAddress = new DepositSourceAddress();
                    sourceAddress.setDepositId(deposit.getId());
                    sourceAddress.setSourceAddress(address);
                    addressesToSave.add(sourceAddress);
                }
                
                sourceAddressRepository.saveAll(addressesToSave);
                log.info("Saved {} source addresses for deposit {}", sourceAddresses.size(), deposit.getId());
                
                // Mark deposit as synced
                deposit.setSrcSynced(true);
                depositRepository.save(deposit);
                log.info("Marked deposit {} as synced", deposit.getId());
            } else {
                log.warn("No source addresses found for deposit {}", deposit.getId());
            }
        } catch (Exception e) {
            log.error("Error processing deposit {}: {}", deposit.getId(), e.getMessage(), e);
        }
    }
} 