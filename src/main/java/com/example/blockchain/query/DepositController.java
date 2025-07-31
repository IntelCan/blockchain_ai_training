package com.example.blockchain.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/deposits")
@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true", matchIfMissing = false)
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @GetMapping("/{txHash}")
    public ResponseEntity<DepositDetailsDto> getDepositByTxHash(@PathVariable String txHash) {
        log.info("Getting deposit details for txHash: {}", txHash);
        
        DepositDetailsDto depositDetails = depositService.getDepositByTxHash(txHash);
        
        if (depositDetails == null) {
            log.warn("Deposit not found for txHash: {}", txHash);
            return ResponseEntity.notFound().build();
        }
        
        log.info("Found deposit with {} source addresses", depositDetails.sourceAddresses().size());
        return ResponseEntity.ok(depositDetails);
    }
} 