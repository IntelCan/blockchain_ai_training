# Zcash Implementation Testing Guide

## Overview
This application now includes a working Zcash blockchain integration using Tatum API.

## Endpoints

### 1. General Tatum Test Endpoint
```
GET /api/tatum-test?network=zec_network&txHash={transaction_hash}
```

### 2. Dedicated Zcash Endpoint
```
GET /api/tatum-test/zcash?txHash={transaction_hash}
```

### 3. Test with Known Transaction
```
GET /api/tatum-test/zcash/test
```



## Example Usage

### Test with the working transaction:
```bash
curl "http://localhost:8080/api/tatum-test/zcash?txHash=8912a2b3f26c95296c909b8dd29e17d53ffae1791fa303894d947889f409bea6"
```

### Test with general endpoint:
```bash
curl "http://localhost:8080/api/tatum-test?network=zec_network&txHash=8912a2b3f26c95296c909b8dd29e17d53ffae1791fa303894d947889f409bea6"
```

### Quick test with known transaction:
```bash
curl "http://localhost:8080/api/tatum-test/zcash/test"
```



## Response Format

The Zcash endpoint returns source addresses from transaction inputs:

```json
[
  "02be29ed5cec57a458d3cc0f12e95a8f8cf9b6d46d3bee908a35b4be58679829a7",
  "02be29ed5cec57a458d3cc0f12e95a8f8cf9b6d46d3bee908a35b4be58679829a7"
]
```

## Implementation Details

- **TatumClient**: Uses JSON-RPC endpoint `https://api.tatum.io/v3/blockchain/node/zcash-mainnet`
- **Method**: `getrawtransaction` with verbose mode (parameter `1`)
- **API Key**: Configured in `application.yml`
- **Network Mapping**: `zec_network` → `zcash`

## Running the Application

1. Start the Spring Boot application:
```bash
./mvnw spring-boot:run
```

2. Test the endpoints using the curl commands above

3. Check logs for detailed transaction information

## Error Handling

- Invalid transaction hash format returns 400 Bad Request
- API errors return 502 Bad Gateway
- All errors are logged with detailed information 