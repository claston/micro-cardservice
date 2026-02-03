package com.sistema.wallet.application.rules;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import com.sistema.wallet.application.exception.WalletInsufficientBalanceException;
import com.sistema.wallet.domain.validation.WalletTransferPolicy;

import java.util.Optional;

public class SufficientBalanceRule implements WalletTransferRule {
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;

    public SufficientBalanceRule(GetAccountBalanceUseCase getAccountBalanceUseCase) {
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
    }

    @Override
    public Optional<WalletRuleViolation> validate(WalletTransferRuleContext context) {
        var balance = getAccountBalanceUseCase.execute(
                context.getTenantId(),
                context.getFromAccount().getLedgerAccountId()
        );
        try {
            new WalletTransferPolicy().validateSufficientBalance(
                    context.getFromAccount().getOwnerType(),
                    balance.getBalanceMinor(),
                    context.getCommand().getAmountMinor()
            );
            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.of(new WalletRuleViolation(new WalletInsufficientBalanceException(ex.getMessage())));
        }
    }
}
