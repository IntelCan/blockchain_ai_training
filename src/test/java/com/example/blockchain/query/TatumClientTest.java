package com.example.blockchain.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TatumClientTest {
    @Autowired
    private TatumClient tatumClient;

    @Test
    public void testFetchTransaction() {
        // Replace with a real blockchain and txHash for your test
        String blockchain = "bitcoin";
        String txHash = "b6f6991d6e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e2e7e";
        String response = tatumClient.fetchTransaction(blockchain, txHash);
        System.out.println("Tatum API response: " + response);
        // Optionally, add assertions based on expected response
    }
} 