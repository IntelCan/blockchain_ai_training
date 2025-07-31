package com.example.blockchain.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TatumClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public TatumClient(WebClient.Builder webClientBuilder, 
                      @Value("${tatum.api-key}") String apiKey,
                      ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.tatum.io")
                .defaultHeader("x-api-key", apiKey)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
        this.objectMapper = objectMapper;
    }

    public Mono<JsonNode> fetchTransaction(String blockchain, String txHash) {
        // Use JSON-RPC endpoint for Dogecoin
        if ("dogecoin".equals(blockchain)) {
            return fetchDogeRawTransactionJsonRpc(txHash);
        }
        
        // Use JSON-RPC endpoint for Zcash
        if ("zcash".equals(blockchain)) {
            return fetchZcashRawTransactionJsonRpc(txHash);
        }
        
        // Use dedicated gateway for Stellar (XLM)
        if ("stellar".equals(blockchain)) {
            return fetchStellarTransaction(txHash);
        }
        
        // Use JSON-RPC endpoint for TRON
        if ("tron".equals(blockchain)) {
            return fetchTronTransactionJsonRpc(txHash);
        }
        

        
        // Use JSON-RPC endpoint for Ethereum Classic
        if ("ethereum-classic".equals(blockchain)) {
            return fetchTransactionJsonRpcEthereumClassic(txHash);
        }
        
        // Use v3 REST API for all other blockchains
        String url = String.format("/v3/%s/transaction/%s", blockchain, txHash);
        
        log.info("Making Tatum API call: GET {}", url);
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("Tatum API raw response: {}", response);
                })
                .map(response -> {
                    log.info("Tatum API response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed JSON response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing JSON response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse JSON response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("Tatum API error: {} - {}", 
                            error.getClass().getSimpleName(), 
                            error.getMessage());
                });
    }
    




    private Mono<JsonNode> fetchTransactionJsonRpcEthereumClassic(String txHash) {
        String jsonRpcRequest = String.format("""
            {
                "jsonrpc": "2.0",
                "method": "eth_getTransactionByHash",
                "params": ["%s"],
                "id": 1
            }
            """, txHash);
        
        log.info("Making Ethereum Classic JSON-RPC call: POST https://ethereum-classic-mainnet.gateway.tatum.io with txHash: {}", txHash);
        
        return WebClient.builder()
                .baseUrl("https://ethereum-classic-mainnet.gateway.tatum.io")
                .defaultHeader("x-api-key", "t-6888ec2b46b339ccd6394228-9d5e06550f9147409b4961d3")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build()
                .post()
                .bodyValue(jsonRpcRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("Ethereum Classic JSON-RPC raw response: {}", response);
                })
                .map(response -> {
                    log.info("Ethereum Classic JSON-RPC response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed Ethereum Classic JSON-RPC response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing Ethereum Classic JSON-RPC response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse Ethereum Classic JSON-RPC response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("Ethereum Classic JSON-RPC error: {} - {}", 
                            error.getClass().getSimpleName(), 
                            error.getMessage());
                });
    }

    // BCH JSON-RPC getrawtransaction
    public Mono<JsonNode> fetchBchRawTransactionJsonRpc(String txid) {
        String jsonRpcRequest = String.format("""
            {\n  \"jsonrpc\": \"2.0\",\n  \"id\": \"1\",\n  \"method\": \"getrawtransaction\",\n  \"params\": [\"%s\", true]\n}\n""", txid);
        log.info("Making BCH JSON-RPC call: POST https://bch-mainnet.gateway.tatum.io getrawtransaction for txid: {}", txid);
        return WebClient.builder()
                .baseUrl("https://bch-mainnet.gateway.tatum.io")
                .defaultHeader("x-api-key", "t-6888ec2b46b339ccd6394228-9d5e06550f9147409b4961d3")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build()
                .post()
                .bodyValue(jsonRpcRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("BCH JSON-RPC raw response: {}", response);
                })
                .map(response -> {
                    log.info("BCH JSON-RPC response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed BCH JSON-RPC response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing BCH JSON-RPC response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse BCH JSON-RPC response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("BCH JSON-RPC error: {} - {}", error.getClass().getSimpleName(), error.getMessage());
                });
    }

    // Dogecoin JSON-RPC getrawtransaction
    public Mono<JsonNode> fetchDogeRawTransactionJsonRpc(String txid) {
        String jsonRpcRequest = String.format("""
            {
                "jsonrpc": "2.0",
                "id": "1",
                "method": "getrawtransaction",
                "params": ["%s", true]
            }
            """, txid);
        
        log.info("Making Dogecoin JSON-RPC call: POST https://doge-mainnet.gateway.tatum.io getrawtransaction for txid: {}", txid);
        
        return WebClient.builder()
                .baseUrl("https://doge-mainnet.gateway.tatum.io")
                .defaultHeader("x-api-key", "t-6888ec2b46b339ccd6394228-9d5e06550f9147409b4961d3")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build()
                .post()
                .bodyValue(jsonRpcRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("Dogecoin JSON-RPC raw response: {}", response);
                })
                .map(response -> {
                    log.info("Dogecoin JSON-RPC response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed Dogecoin JSON-RPC response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing Dogecoin JSON-RPC response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse Dogecoin JSON-RPC response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("Dogecoin JSON-RPC error: {} - {}", 
                            error.getClass().getSimpleName(), 
                            error.getMessage());
                });
    }

    // Zcash JSON-RPC getrawtransaction
    public Mono<JsonNode> fetchZcashRawTransactionJsonRpc(String txid) {
        String jsonRpcRequest = String.format("""
            {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "getrawtransaction",
                "params": ["%s", 1]
            }
            """, txid);
        
        log.info("Making Zcash JSON-RPC call: POST https://api.tatum.io/v3/blockchain/node/zcash-mainnet getrawtransaction for txid: {}", txid);
        
        return WebClient.builder()
                .baseUrl("https://api.tatum.io/v3/blockchain/node/zcash-mainnet")
                .defaultHeader("x-api-key", "t-6888ec2b46b339ccd6394228-9d5e06550f9147409b4961d3")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build()
                .post()
                .bodyValue(jsonRpcRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("Zcash JSON-RPC raw response: {}", response);
                })
                .map(response -> {
                    log.info("Zcash JSON-RPC response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed Zcash JSON-RPC response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing Zcash JSON-RPC response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse Zcash JSON-RPC response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("Zcash JSON-RPC error: {} - {}", 
                            error.getClass().getSimpleName(), 
                            error.getMessage());
                });
    }

    // TRON JSON-RPC eth_getTransactionByHash
    public Mono<JsonNode> fetchTronTransactionJsonRpc(String txHash) {
        String jsonRpcRequest = String.format("""
            {
                "jsonrpc": "2.0",
                "method": "eth_getTransactionByHash",
                "params": ["%s"],
                "id": 1
            }
            """, txHash);
        
        log.info("Making TRON JSON-RPC call: POST https://tron-mainnet.gateway.tatum.io/jsonrpc/ eth_getTransactionByHash for txHash: {}", txHash);
        
        return WebClient.builder()
                .baseUrl("https://tron-mainnet.gateway.tatum.io")
                .defaultHeader("x-api-key", "t-6888ec2b46b339ccd6394228-9d5e06550f9147409b4961d3")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build()
                .post()
                .uri("/jsonrpc/")
                .bodyValue(jsonRpcRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("TRON JSON-RPC raw response: {}", response);
                })
                .map(response -> {
                    log.info("TRON JSON-RPC response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed TRON JSON-RPC response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing TRON JSON-RPC response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse TRON JSON-RPC response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("TRON JSON-RPC error: {} - {}", 
                            error.getClass().getSimpleName(), 
                            error.getMessage());
                });
    }

    // Stellar REST API
    public Mono<JsonNode> fetchStellarTransaction(String txid) {
        log.info("Making Stellar REST API call: GET https://stellar-mainnet.gateway.tatum.io/transactions/{}", txid);
        
        return WebClient.builder()
                .baseUrl("https://stellar-mainnet.gateway.tatum.io")
                .defaultHeader("x-api-key", "t-6888ec2b46b339ccd6394228-9d5e06550f9147409b4961d3")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build()
                .get()
                .uri("/transactions/{txid}", txid)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(30))
                .doOnNext(response -> {
                    log.info("Stellar REST API raw response: {}", response);
                })
                .map(response -> {
                    log.info("Stellar REST API response length: {}", response.length());
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        log.info("Successfully parsed Stellar REST API response");
                        return jsonNode;
                    } catch (Exception e) {
                        log.error("Error parsing Stellar REST API response: {}", e.getMessage(), e);
                        throw new RuntimeException("Failed to parse Stellar REST API response", e);
                    }
                })
                .doOnError(error -> {
                    log.error("Stellar REST API error: {} - {}", 
                            error.getClass().getSimpleName(), 
                            error.getMessage());
                });
    }




} 