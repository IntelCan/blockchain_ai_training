package com.example.blockchain.query;

import java.util.List;

public record DepositDetailsDto(Deposit deposit,
                               List<DepositSourceAddress> sourceAddresses) {
} 