package com.example.blockchain.query;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TransactionLookupService {

    private final TatumClient tatumClient;

    public TransactionLookupService(TatumClient tatumClient) {
        this.tatumClient = tatumClient;
    }

    public Mono<JsonNode> lookupTransaction(String network, String txHash) {
        String tatumBlockchain = NetworkMapper.toTatumBlockchain(network);
        
        if ("UNSUPPORTED".equals(tatumBlockchain)) {
            log.error("Unsupported network: {}", network);
            return Mono.error(new UnsupportedOperationException("Unsupported network: " + network));
        }
        
        log.info("Looking up transaction for network: {} -> tatum: {}, txHash: {}", network, tatumBlockchain, txHash);
        return tatumClient.fetchTransaction(tatumBlockchain, txHash);
    }
} 