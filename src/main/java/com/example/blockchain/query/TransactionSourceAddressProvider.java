package com.example.blockchain.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionSourceAddressProvider {
    private final TatumClient tatumClient;
    private final TransactionSourceAddressExtractor transactionSourceAddressExtractor;

    public List<String> provide(String tatumBlockchain, String txHash) {
        return tatumClient.fetchTransaction(tatumBlockchain, txHash)
                .onErrorResume(throwable -> {
                    log.error("Tatum API error: {} - {}", throwable.getClass().getSimpleName(), throwable.getMessage());
                    return Mono.empty();
                })
                .map(jsonResponse -> {
                    log.info("Parsed JSON keys: {}", jsonResponse.fieldNames());
                    log.info("Full JSON response: {}", jsonResponse.toPrettyString());

                    List<String> addresses = transactionSourceAddressExtractor.extractSourceAddresses(jsonResponse, tatumBlockchain);
                    log.info("Returning {} addresses total", addresses.size());
                    log.info("Extracted addresses: {}", addresses);

                    return addresses;
                })
                .switchIfEmpty(Mono.just(List.of()))
                .block();
    }
}
