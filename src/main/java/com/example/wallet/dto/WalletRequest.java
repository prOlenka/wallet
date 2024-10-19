package com.example.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletRequest {
    private UUID walletId;
    private String operationType; // "DEPOSIT" or "WITHDRAW"
    private BigDecimal amount;
}