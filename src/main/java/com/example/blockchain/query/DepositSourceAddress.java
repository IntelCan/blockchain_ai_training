package com.example.blockchain.query;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "source_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositSourceAddress {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "deposit_id", nullable = false)
    private UUID depositId;

    @Column(name = "source_address", nullable = false)
    private String sourceAddress;
} 