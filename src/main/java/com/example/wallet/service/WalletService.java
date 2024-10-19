package com.example.wallet.service;

import com.example.wallet.dto.WalletRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {

    private WalletRepository walletRepository;

    @Transactional
    public WalletResponse processTransaction(WalletRequest request) {
        Wallet wallet = walletRepository.findByWalletId(request.getWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        BigDecimal newAmount = switch (request.getOperationType()) {
            case "DEPOSIT" -> wallet.getAmount().add(request.getAmount());
            case "WITHDRAW" -> {
                if (wallet.getAmount().compareTo(request.getAmount()) < 0) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                yield wallet.getAmount().subtract(request.getAmount());
            }
            default -> throw new IllegalArgumentException("Invalid operation type");
        };

        wallet.setAmount(newAmount);
        walletRepository.save(wallet);

        return new WalletResponse(wallet.getWalletId(), wallet.getAmount());
    }

    public WalletResponse getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        return new WalletResponse(wallet.getWalletId(), wallet.getAmount());
    }

    @Transactional
    public WalletResponse performOperation(WalletRequest request) {
        UUID walletId = request.getWalletId();
        String operationType = request.getOperationType();
        BigDecimal amount = request.getAmount();

        // Validate the input parameters
        if (walletId == null || operationType == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid request parameters");
        }

        // Retrieve the wallet from the repository
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // Perform the operation based on the operation type
        if ("DEPOSIT".equalsIgnoreCase(operationType)) {
            wallet.setAmount(wallet.getAmount().add(amount));
        } else if ("WITHDRAW".equalsIgnoreCase(operationType)) {
            if (wallet.getAmount().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            wallet.setAmount(wallet.getAmount().subtract(amount));
        } else {
            throw new IllegalArgumentException("Invalid operation type");
        }

        // Save the updated wallet back to the repository
        walletRepository.save(wallet);

        // Return the updated wallet balance
        return new WalletResponse(wallet.getWalletId(), wallet.getAmount());
    }
}
