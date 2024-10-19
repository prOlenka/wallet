package com.example.wallet.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WalletResponse {
    private UUID walletId;
    private BigDecimal balance;

    public WalletResponse(UUID walletId, BigDecimal balance) {
        this.walletId = walletId;
        this.balance = balance;
    }
}
