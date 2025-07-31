package com.example.blockchain.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.UUID;
import static org.mockito.Mockito.*;

public class DepositSyncServiceTest {
    @Mock
    private DepositRepository depositRepository;
    @Mock
    private DepositSourceAddressRepository sourceAddressRepository;
    @Mock
    private TatumClient tatumClient;
    @InjectMocks
    private DepositSyncService depositSyncService;

    public DepositSyncServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUnsupportedNetwork() {
        Deposit deposit = new Deposit(UUID.randomUUID(), "txid", "unsupported_network", "txhash", false);
        when(depositRepository.findAllBySrcSyncedFalse()).thenReturn(Collections.singletonList(deposit));
        depositSyncService.syncDeposits();
        verify(sourceAddressRepository, never()).save(any());
        verify(depositRepository, never()).save(any());
    }
} 