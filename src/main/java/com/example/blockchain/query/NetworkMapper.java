package com.example.blockchain.query;

import java.util.HashMap;
import java.util.Map;

public class NetworkMapper {
    private static final Map<String, String> NETWORK_TO_TATUM = new HashMap<>();
    static {
        NETWORK_TO_TATUM.put("ada_network", "ada");
        NETWORK_TO_TATUM.put("algo_network", "algorand");
        NETWORK_TO_TATUM.put("bch_network", "bcash");
//        NETWORK_TO_TATUM.put("bsv_network", "bitcoin-sv");
        NETWORK_TO_TATUM.put("btc_network", "bitcoin");
        NETWORK_TO_TATUM.put("c_chain", "avalanche");
//        NETWORK_TO_TATUM.put("cosmos_network", "cosmos");
//        NETWORK_TO_TATUM.put("dash_network", "dash");
        NETWORK_TO_TATUM.put("doge_network", "dogecoin");
        // NETWORK_TO_TATUM.put("dot_network", "polkadot-mainnet"); // Temporarily disabled - no direct txHash lookup support
        // NETWORK_TO_TATUM.put("eos_network", "eos-mainnet"); // Temporarily disabled - gateway not working
        NETWORK_TO_TATUM.put("polygon_network", "polygon");
        NETWORK_TO_TATUM.put("eth_network", "ethereum");
        NETWORK_TO_TATUM.put("flare_network", "flare");
        NETWORK_TO_TATUM.put("ftm_network", "fantom");
//        NETWORK_TO_TATUM.put("ksm_network", "kusama");
//        NETWORK_TO_TATUM.put("lisk_network", "lisk");
        NETWORK_TO_TATUM.put("ltc_network", "litecoin");
//        NETWORK_TO_TATUM.put("celestia_network", "celestia");
//        NETWORK_TO_TATUM.put("luna_network", "luna");
//        NETWORK_TO_TATUM.put("sgb_network", "songbird");
        NETWORK_TO_TATUM.put("solana_network", "solana");
        NETWORK_TO_TATUM.put("trx_network", "tron");
        NETWORK_TO_TATUM.put("xlm_network", "stellar");
        NETWORK_TO_TATUM.put("xrp_network", "xrp");
        NETWORK_TO_TATUM.put("zec_network", "zcash");
//        NETWORK_TO_TATUM.put("near_network", "near");
//        NETWORK_TO_TATUM.put("tezos_network", "tezos");
        NETWORK_TO_TATUM.put("arc20_network", "arb");
        NETWORK_TO_TATUM.put("sonic_network", "sonic");
        NETWORK_TO_TATUM.put("optimism_blockchain", "optimism");
//        NETWORK_TO_TATUM.put("inj_blockchain", "injective");
        NETWORK_TO_TATUM.put("bera_blockchain", "berachain");
        // NETWORK_TO_TATUM.put("ton_blockchain", "ton"); // Temporarily disabled
    }

    public static String toTatumBlockchain(String network) {
        return NETWORK_TO_TATUM.getOrDefault(network, "UNSUPPORTED");
    }
} 