package com.example.blockchain.query;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync-deposits")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.datasource.url", havingValue = "true", matchIfMissing = false)
public class DepositSyncController {
    private final DepositSyncService depositSyncService;

    @PostMapping
    public ResponseEntity<String> syncDeposits(@RequestHeader(value = "X-API-KEY", required = false) String apiKey) {
        // Simple demo security: require header X-API-KEY=demo123
        if (apiKey == null || !apiKey.equals("demo123")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        depositSyncService.syncDeposits();
        return ResponseEntity.ok("Sync started (batch runs synchronously)");
    }
} 