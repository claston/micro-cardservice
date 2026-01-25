package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.wallet.application.model.WalletBalance;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class GetWalletBalanceUseCase {
    private final WalletAccountRepository walletAccountRepository;
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;

    public GetWalletBalanceUseCase(WalletAccountRepository walletAccountRepository,
                                   GetAccountBalanceUseCase getAccountBalanceUseCase) {
        this.walletAccountRepository = walletAccountRepository;
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
    }

    public WalletBalance execute(UUID tenantId, UUID accountId) {
        WalletAccount walletAccount = walletAccountRepository.findById(tenantId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("wallet account not found: " + accountId));
        var balance = getAccountBalanceUseCase.execute(tenantId, walletAccount.getLedgerAccountId());
        return new WalletBalance(walletAccount.getId(), balance.getBalanceMinor(), balance.getCurrency());
    }
}
