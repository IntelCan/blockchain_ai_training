package com.example.blockchain.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.common.util.concurrent.RateLimiter;
import java.util.*;

@RestController
@RequestMapping("/api/batch-tatum-test")
@RequiredArgsConstructor
@Slf4j
public class BatchTatumTestController {
    private final TatumTestController tatumTestController;
    private final RateLimiter rateLimiter = RateLimiter.create(3.0); // 3 requests per second

    @GetMapping
    public ResponseEntity<?> batchTestTatum() {
        // CSV data provided by user
        List<TestEntry> testEntries = Arrays.asList(
            new TestEntry("ada_network", "323263f61e6f49a6f57362a6a1c22fbcb8d691968cb42360a8dfa567888869b2"),
            new TestEntry("algo_network", "IITPB4MTT3BUAQ4V4HFMB2PM6V23D53TXU6GXQBCX2ZN4N6NVDFA"),
            new TestEntry("arc20_network", "0xfda3dc9257e47e438b966e35ce759c7ac43215abad88273da585fd323bdfcff2"),
            new TestEntry("bch_network", "d506f6779db60b5ed4045f929c49d77d9ddb7d7bff92b56090d39702beb82c68"),
            new TestEntry("bsv_network", "0b6c4b7df5c1ced5ea1c43b19f82de911632b19ecf14a2855aa1f07ef2d88dfe"),
            new TestEntry("btc_network", "a7c6ae929d2f4a65b223c54b7b6a94455c7e630f4f0251073f64fd058d900876"),
            new TestEntry("btg_network", "95b35803d8ff595f7333da353adcf13fad2a94304f311b1d607ea1151bbd0813"),
            new TestEntry("c_chain", "0x400d847c0ba7c2068eba73851f28eb0abb76530e52d222b7f4be20894d9f69ed"),
            new TestEntry("celestia_network", "204E81402122A022A8521283FA404F017DB5E949D61794AAD2CD04DCE10A6F59"),
            new TestEntry("cosmos_network", "77390D17F4829659CA88355697C5124A95EB8D59ABCBB4E2009B89757DE2788F"),
            new TestEntry("crc20_chiliz_2_network", "0x59a0c53290b58987e7eb56ca8758614d118509ee2c2649697563a033d505b712"),
            new TestEntry("crc20_network", "0x454317ea6f90dcda23d8ee52477b209ea637b198682d440547b490ed24d4b2b5"),
            new TestEntry("dash_network", "77ef4b119c55f650bac977dc03b26f75f03e5f2322d9e57191657ac182c50998"),
            new TestEntry("doge_network", "b2b6d8f598b3b67e8df0909510bd50f3ad565c2529d13c5d00f0ccfb27015f6c"),
            new TestEntry("dot_network", "0x97df2ca295b95393287505ada9cc13124a6c777b184d236dec3ffe947a29030b"),
            new TestEntry("eos_network", "ae96dea04d967bc0556abb529c192649ae7776c69eb34cb1b1f3f24e90642069"),
            new TestEntry("eth_network", "0xcad36a9d598eeff38fead5359a62964ee644dd24ce78c485691a19baff6711b2"),
            new TestEntry("flare_network", "0x8e83cb9cff25adfcee37be866d1ff4b870a1a3fc1ad06fbee6477e081d8a0074"),
            new TestEntry("ftm_network", "0x44b03777e9df52feb771ff600c9315e0ae954152453cfd0216a0bf50aa5e122a"),
            new TestEntry("inj_blockchain", "125BECBCDB4C45E8AA932D2BE76CE93AD4D8EC0B9F91B350CB43DE051D6AF315"),
            new TestEntry("ksm_network", "0xce1ad8c09bf448b121d41b65998a1777206094ab66faafea01d8a8a9ef53fec1"),
            new TestEntry("lisk_network", "b408ba3ce85f7a598dfafa2dbdfb55eff0096acb9f2d1a276609cd2134a5d0f3"),
            new TestEntry("ltc_network", "e820c4dda82e77d1c3cb26a0d60d2b0759ce418f7c7332fe993f7b65cf9d7a38"),
            new TestEntry("luna_network", "F2BD49FC398C79479A1CAA3269E6F0D8491F5B5B0F4C3478BA85AE24CF91F9FA"),
            new TestEntry("near_network", "Gnyo33u6zAMKozTHaH9xvQ9J4rMQRpqcYfx6YRZ7xCYH"),
            new TestEntry("optimism_blockchain", "0xb4faf443d1487805f6505ee7c722ceebcf28868f73acc25ce75912b3a0a8943b"),
            new TestEntry("polygon_network", "0xe30bdf0f9d0759a2e94700881f4d80303b155f518809378c03d4d313ee2a3f68"),
            new TestEntry("sgb_network", "0x328717e6890fe58b672aa0b53c2e8f940d59c9e8e2f25da83509670ba322a4f7"),
            new TestEntry("solana_network", "4XTnyHdK9gZLikNmB3jpSjkWh34TXwovKR45Dnb83QvZu7kEULWv3a3VGSfrrmH6yVMrpvnMWRd8G843uFGPH3P3"),
            new TestEntry("sonic_network", "0x35fafaef4318d4b95cc9168b024fb7be764d2fab5d947c735c544ec65465aab0"),
            new TestEntry("tezos_network", "oouVXVG4b8pbDxLDXfd4bTq1XNXNkMnY2gFjX5Xv1UmRKjeWxMi"),
            // new TestEntry("ton_blockchain", "f6e3caccea2490504cbf0d051ced449f054e3f3d05ebcbcfc5b55ca2b899fbb3"), // Temporarily disabled
            new TestEntry("trx_network", "06f85d481cab9b68133c917a7941429768d7dcc9ca05a26cd30f0e5238e6f764"),
            new TestEntry("xlm_network", "7c9b155297e81f9ebf76ad3af5b60192c0a5e4251f61328309ff3233eb13e15d"),
            new TestEntry("xrp_network", "6C77F7ABC79B9D3181C03CA74012784EE5318DB01C961A8536E8453DBE2BC71E"),
            new TestEntry("zec_network", "8912a2b3f26c95296c909b8dd29e17d53ffae1791fa303894d947889f409bea6")
        );

        List<BatchResult> results = new ArrayList<>();
        
        for (TestEntry entry : testEntries) {
            try {
                log.info("Testing entry: {} - {}", entry.network, entry.txHash);
                
                // Apply rate limiting - wait if necessary
                rateLimiter.acquire();
                
                ResponseEntity<?> response = tatumTestController.testTatum(entry.network, entry.txHash);
                
                BatchResult result = new BatchResult(
                    entry.network,
                    entry.txHash,
                    response.getStatusCodeValue(),
                    response.getBody()
                );
                
                results.add(result);
                log.info("Result for {}: {}", entry.network, result);
                
            } catch (Exception e) {
                log.error("Error testing entry {} - {}: {}", entry.network, entry.txHash, e.getMessage());
                
                BatchResult result = new BatchResult(
                    entry.network,
                    entry.txHash,
                    500,
                    "Error: " + e.getMessage()
                );
                
                results.add(result);
            }
        }
        
        return ResponseEntity.ok(results);
    }

    // Data classes
    public static class TestEntry {
        public final String network;
        public final String txHash;
        
        public TestEntry(String network, String txHash) {
            this.network = network;
            this.txHash = txHash;
        }
    }
    
    public static class BatchResult {
        public final String network;
        public final String txHash;
        public final int statusCode;
        public final Object result;
        
        public BatchResult(String network, String txHash, int statusCode, Object result) {
            this.network = network;
            this.txHash = txHash;
            this.statusCode = statusCode;
            this.result = result;
        }
        
        @Override
        public String toString() {
            return String.format("BatchResult{network='%s', txHash='%s', statusCode=%d, result=%s}", 
                network, txHash, statusCode, result);
        }
    }
} 