package com.example.blockchain.query;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionSourceAddressExtractor {
    private final TatumClient tatumClient;

    public List<String> extractSourceAddresses(JsonNode json, String tatumBlockchain) {
        Set<String> addresses = new LinkedHashSet<>();

        try {
            // BCH: fetch source addresses from previous outputs using JSON-RPC
            if ("bcash".equals(tatumBlockchain) && json.has("vin") && json.get("vin").isArray()) {
                JsonNode vin = json.get("vin");
                for (JsonNode input : vin) {
                    if (input.has("txid") && input.has("vout")) {
                        String prevTxId = input.get("txid").asText();
                        int voutIndex = input.get("vout").asInt();
                        try {
                            // Use CompletableFuture to run in separate thread to avoid blocking reactive thread
                            CompletableFuture<JsonNode> future = CompletableFuture.supplyAsync(() -> {
                                try {
                                    return tatumClient.fetchBchRawTransactionJsonRpc(prevTxId).block();
                                } catch (Exception e) {
                                    log.warn("Error in async BCH call for {}:{}: {}", prevTxId, voutIndex, e.getMessage());
                                    return null;
                                }
                            });

                            JsonNode prevTxResp = future.get(10, TimeUnit.SECONDS); // 10 second timeout
                            if (prevTxResp != null && prevTxResp.has("result")) {
                                JsonNode prevTx = prevTxResp.get("result");
                                if (prevTx.has("vout") && prevTx.get("vout").isArray()) {
                                    JsonNode vouts = prevTx.get("vout");
                                    for (JsonNode vout : vouts) {
                                        if (vout.has("n") && vout.get("n").asInt() == voutIndex) {
                                            if (vout.has("scriptPubKey") && vout.get("scriptPubKey").has("addresses")) {
                                                JsonNode addrArr = vout.get("scriptPubKey").get("addresses");
                                                for (JsonNode addr : addrArr) {
                                                    String address = addr.asText();
                                                    if (address != null && !address.isEmpty()) {
                                                        addresses.add(address);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.warn("Error fetching prevout for BCH input {}:{}: {}", prevTxId, voutIndex, ex.getMessage());
                        }
                    }
                }
                return new ArrayList<>(addresses);
            }



            // Dogecoin: fetch source addresses from previous outputs using JSON-RPC
            if ("dogecoin".equals(tatumBlockchain) && json.has("result") && json.get("result").has("vin") && json.get("result").get("vin").isArray()) {
                JsonNode vin = json.get("result").get("vin");
                for (JsonNode input : vin) {
                    if (input.has("txid") && input.has("vout")) {
                        String prevTxId = input.get("txid").asText();
                        int voutIndex = input.get("vout").asInt();
                        log.info("Fetching previous Dogecoin transaction: {} with vout: {}", prevTxId, voutIndex);
                        try {
                            // Use CompletableFuture to run in separate thread to avoid blocking reactive thread
                            CompletableFuture<JsonNode> future = CompletableFuture.supplyAsync(() -> {
                                try {
                                    return tatumClient.fetchDogeRawTransactionJsonRpc(prevTxId).block();
                                } catch (Exception e) {
                                    log.warn("Error in async Dogecoin call for {}:{}: {}", prevTxId, voutIndex, e.getMessage());
                                    return null;
                                }
                            });

                            JsonNode prevTxResp = future.get(10, TimeUnit.SECONDS); // 10 second timeout
                            if (prevTxResp != null && prevTxResp.has("result")) {
                                JsonNode prevTx = prevTxResp.get("result");
                                if (prevTx.has("vout") && prevTx.get("vout").isArray()) {
                                    JsonNode vouts = prevTx.get("vout");
                                    for (JsonNode vout : vouts) {
                                        if (vout.has("n") && vout.get("n").asInt() == voutIndex) {
                                            if (vout.has("scriptPubKey") && vout.get("scriptPubKey").has("addresses")) {
                                                JsonNode addrArr = vout.get("scriptPubKey").get("addresses");
                                                for (JsonNode addr : addrArr) {
                                                    String address = addr.asText();
                                                    if (address != null && !address.isEmpty()) {
                                                        addresses.add(address);
                                                        log.info("Found Dogecoin source address: {}", address);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.warn("Error fetching prevout for Dogecoin input {}:{}: {}", prevTxId, voutIndex, ex.getMessage());
                        }
                    }
                }
                return new ArrayList<>(addresses);
            }

            // Zcash: fetch source addresses from previous outputs using JSON-RPC
            if ("zcash".equals(tatumBlockchain) && json.has("result") && json.get("result").has("vin") && json.get("result").get("vin").isArray()) {
                JsonNode vin = json.get("result").get("vin");
                for (JsonNode input : vin) {
                    if (input.has("txid") && input.has("vout")) {
                        String prevTxId = input.get("txid").asText();
                        int voutIndex = input.get("vout").asInt();
                        log.info("Fetching previous Zcash transaction: {} with vout: {}", prevTxId, voutIndex);
                        try {
                            // Use CompletableFuture to run in separate thread to avoid blocking reactive thread
                            CompletableFuture<JsonNode> future = CompletableFuture.supplyAsync(() -> {
                                try {
                                    return tatumClient.fetchZcashRawTransactionJsonRpc(prevTxId).block();
                                } catch (Exception e) {
                                    log.warn("Error in async Zcash call for {}:{}: {}", prevTxId, voutIndex, e.getMessage());
                                    return null;
                                }
                            });

                            JsonNode prevTxResp = future.get(10, TimeUnit.SECONDS); // 10 second timeout
                            if (prevTxResp != null && prevTxResp.has("result")) {
                                JsonNode prevTx = prevTxResp.get("result");
                                if (prevTx.has("vout") && prevTx.get("vout").isArray()) {
                                    JsonNode vouts = prevTx.get("vout");
                                    for (JsonNode vout : vouts) {
                                        if (vout.has("n") && vout.get("n").asInt() == voutIndex) {
                                            if (vout.has("scriptPubKey") && vout.get("scriptPubKey").has("addresses")) {
                                                JsonNode addrArr = vout.get("scriptPubKey").get("addresses");
                                                for (JsonNode addr : addrArr) {
                                                    String address = addr.asText();
                                                    if (address != null && !address.isEmpty()) {
                                                        addresses.add(address);
                                                        log.info("Found Zcash source address: {}", address);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.warn("Error fetching prevout for Zcash input {}:{}: {}", prevTxId, voutIndex, ex.getMessage());
                        }
                    }
                }
                return new ArrayList<>(addresses);
            }

            // XRP: extract Account field from REST API response
            if ("xrp".equals(tatumBlockchain) && json.has("Account")) {
                String account = json.get("Account").asText();
                if (account != null && !account.isEmpty()) {
                    addresses.add(account);
                    log.info("Found XRP source account: {}", account);
                }
                return new ArrayList<>(addresses);
            }

            // Stellar: extract source_account from REST API response
            if ("stellar".equals(tatumBlockchain) && json.has("source_account")) {
                String sourceAccount = json.get("source_account").asText();
                if (sourceAccount != null && !sourceAccount.isEmpty()) {
                    addresses.add(sourceAccount);
                    log.info("Found Stellar source account: {}", sourceAccount);
                }
                return new ArrayList<>(addresses);
            }

            // TRON: extract from field from JSON-RPC response
            if ("tron".equals(tatumBlockchain) && json.has("result") && json.get("result").has("from")) {
                String fromAddress = json.get("result").get("from").asText();
                if (fromAddress != null && !fromAddress.isEmpty()) {
                    addresses.add(fromAddress);
                    log.info("Found TRON source address: {}", fromAddress);
                }
                return new ArrayList<>(addresses);
            }



            // Handle JSON-RPC responses (for EVM chains)
            if (json.has("jsonrpc") && json.has("result")) {
                JsonNode result = json.get("result");
                if (result != null && !result.isNull()) {
                    // Extract 'from' address for EVM chains
                    if (result.has("from")) {
                        String fromAddress = result.get("from").asText();
                        if (fromAddress != null && !fromAddress.isEmpty()) {
                            addresses.add(fromAddress);
                        }
                    }
                }
                return new ArrayList<>(addresses);
            }

            // Handle UTXO chains (Bitcoin, Litecoin, Dogecoin, etc.)
            if (json.has("inputs") && json.get("inputs").isArray()) {
                JsonNode inputs = json.get("inputs");
                for (JsonNode input : inputs) {
                    // Check for direct address field
                    if (input.has("address")) {
                        String address = input.get("address").asText();
                        if (address != null && !address.isEmpty()) {
                            addresses.add(address);
                        }
                    }
                    // Check for nested coin.address field
                    if (input.has("coin") && input.get("coin").has("address")) {
                        String address = input.get("coin").get("address").asText();
                        if (address != null && !address.isEmpty()) {
                            addresses.add(address);
                        }
                    }
                }
                return new ArrayList<>(addresses);
            }

            // Handle UTXO chains with 'vin' field (Bitcoin Cash, etc.)
            if (json.has("vin") && json.get("vin").isArray()) {
                JsonNode vin = json.get("vin");
                for (JsonNode input : vin) {
                    // For Bitcoin Cash, we need to extract addresses from prevout.addresses
                    if (input.has("prevout") && input.get("prevout").has("addresses")) {
                        JsonNode addressesNode = input.get("prevout").get("addresses");
                        if (addressesNode.isArray()) {
                            for (JsonNode addressNode : addressesNode) {
                                String address = addressNode.asText();
                                if (address != null && !address.isEmpty()) {
                                    addresses.add(address);
                                }
                            }
                        }
                    }
                    // Fallback: extract public key from scriptSig.asm if prevout.addresses not available
                    if (input.has("scriptSig") && input.get("scriptSig").has("asm")) {
                        String asm = input.get("scriptSig").get("asm").asText();
                        // Extract public key from scriptSig.asm (last part after space)
                        String[] parts = asm.split(" ");
                        if (parts.length > 0) {
                            String publicKey = parts[parts.length - 1];
                            if (publicKey.length() == 66 && (publicKey.startsWith("02") || publicKey.startsWith("03"))) {
                                // This is a compressed public key, we can derive address from it
                                // For now, let's add the public key as a placeholder
                                addresses.add("PK:" + publicKey);
                            }
                        }
                    }
                }
                return new ArrayList<>(addresses);
            }

            // Solana: extract source address from accountKeys
            if ("solana".equals(tatumBlockchain)) {
                log.info("Processing Solana transaction data");

                // Check for accountKeys in the main response
                if (json.has("accountKeys") && json.get("accountKeys").isArray()) {
                    JsonNode accountKeys = json.get("accountKeys");
                    log.info("Found {} account keys in main response", accountKeys.size());
                    for (JsonNode accountKey : accountKeys) {
                        String address = accountKey.asText();
                        if (address != null && !address.isEmpty()) {
                            addresses.add(address);
                            log.info("Added Solana address from accountKeys: {}", address);
                        }
                    }
                }

                // Check for accountKeys in transaction.message
                if (json.has("transaction") && json.get("transaction").has("message") &&
                        json.get("transaction").get("message").has("accountKeys")) {
                    JsonNode accountKeys = json.get("transaction").get("message").get("accountKeys");
                    log.info("Found {} account keys in transaction.message", accountKeys.size());

                    // Get header info to determine which accounts are user accounts
                    JsonNode header = json.get("transaction").get("message").get("header");
                    int numRequiredSignatures = header.has("numRequiredSignatures") ? header.get("numRequiredSignatures").asInt() : 0;
                    int numReadonlySignedAccounts = header.has("numReadonlySignedAccounts") ? header.get("numReadonlySignedAccounts").asInt() : 0;

                    log.info("Solana header: numRequiredSignatures={}, numReadonlySignedAccounts={}", numRequiredSignatures, numReadonlySignedAccounts);

                    // Add only user accounts (first numRequiredSignatures accounts, excluding readonly signed accounts)
                    int userAccountCount = numRequiredSignatures - numReadonlySignedAccounts;
                    for (int i = 0; i < Math.min(userAccountCount, accountKeys.size()); i++) {
                        String address = accountKeys.get(i).asText();
                        if (address != null && !address.isEmpty() && !address.startsWith("11111111111111111111111111111111") && !address.startsWith("Sysvar")) {
                            addresses.add(address);
                            log.info("Added Solana user address from transaction.message.accountKeys[{}]: {}", i, address);
                        }
                    }
                }

                // Check for accountKeys in result.transaction.message
                if (json.has("result") && json.get("result").has("transaction") &&
                        json.get("result").get("transaction").has("message") &&
                        json.get("result").get("transaction").get("message").has("accountKeys")) {
                    JsonNode accountKeys = json.get("result").get("transaction").get("message").get("accountKeys");
                    log.info("Found {} account keys in result.transaction.message", accountKeys.size());
                    for (JsonNode accountKey : accountKeys) {
                        String address = accountKey.asText();
                        if (address != null && !address.isEmpty()) {
                            addresses.add(address);
                            log.info("Added Solana address from result.transaction.message.accountKeys: {}", address);
                        }
                    }
                }

                // If we found addresses, return them
                if (!addresses.isEmpty()) {
                    log.info("Returning {} Solana addresses", addresses.size());
                    return new ArrayList<>(addresses);
                }

                // Log the full response structure for debugging
                log.info("Solana response structure: {}", json.toPrettyString());
            }

            // Handle other common fields
            String[] commonFields = {"from", "fromAddress", "fromAccount", "sourceAccount", "sender"};
            for (String field : commonFields) {
                if (json.has(field)) {
                    String address = json.get(field).asText();
                    if (address != null && !address.isEmpty()) {
                        addresses.add(address);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error extracting source addresses: {}", e.getMessage(), e);
        }

        return new ArrayList<>(addresses);
    }

}
