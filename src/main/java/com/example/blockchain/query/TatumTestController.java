package com.example.blockchain.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/tatum-test")
public class TatumTestController {
    private final TransactionSourceAddressProvider transactionSourceAddressProvider;

    public TatumTestController(TransactionSourceAddressProvider transactionSourceAddressProvider) {
        this.transactionSourceAddressProvider = transactionSourceAddressProvider;
    }

    @GetMapping
    public ResponseEntity<?> testTatum(@RequestParam String network, @RequestParam String txHash) {
        log.info("Testing Tatum API for network: {}, txHash: {}", network, txHash);

        if (!isValidTxHash(txHash)) {
            return ResponseEntity.badRequest().body("Invalid txHash format for " + network);
        }

        String tatumBlockchain = NetworkMapper.toTatumBlockchain(network);
        log.info("Mapped to Tatum blockchain: {}", tatumBlockchain);

        if ("UNSUPPORTED".equals(tatumBlockchain)) {
            return ResponseEntity.badRequest().body("Unsupported network: " + network);
        }
        
        try {
            return ResponseEntity.ok().body( transactionSourceAddressProvider.provide(tatumBlockchain, txHash));
        } catch (Exception e) {
            log.error("Error testing Tatum API: {}", e.getMessage(), e);
            return ResponseEntity.status(502).body("No response from Tatum");
        }
    }

    private boolean isValidTxHash(String txHash) {
        // Basic validation for different blockchain formats
        if (txHash == null || txHash.trim().isEmpty()) {
            return false;
        }
        
        // Ethereum-like (0x + 64 hex chars)
        if (txHash.startsWith("0x") && Pattern.matches("^0x[a-fA-F0-9]{64}$", txHash)) {
            return true;
        }
        
        // Bitcoin-like (64 hex chars)
        if (Pattern.matches("^[a-fA-F0-9]{64}$", txHash)) {
            return true;
        }
        
        // Algorand (base32)
        if (Pattern.matches("^[A-Z2-7]{52}$", txHash)) {
            return true;
        }
        
        // Solana (base58)
        if (Pattern.matches("^[1-9A-HJ-NP-Za-km-z]{32,44}$", txHash)) {
            return true;
        }
        
        // Near (base58)
        if (Pattern.matches("^[1-9A-HJ-NP-Za-km-z]{44,}$", txHash)) {
            return true;
        }
        
        // Lisk (numeric)
        if (Pattern.matches("^\\d+$", txHash)) {
            return true;
        }
        
        // Tezos (base58)
        if (Pattern.matches("^[1-9A-HJ-NP-Za-km-z]{50,}$", txHash)) {
            return true;
        }
        
        return true; // Allow other formats
    }
} 
