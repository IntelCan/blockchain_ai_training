package com.example.blockchain.query;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "deposits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String network;

    @Column(nullable = false)
    private String txHash;

    @Column(name = "src_synced", nullable = false)
    private boolean srcSynced = false;
} 