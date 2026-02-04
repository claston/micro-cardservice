package com.sistema.wallet.application.rules;

import com.sistema.ledger.application.GetAccountBalanceUseCase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class WalletTransferRulesPipeline {
    private final List<WalletTransferRule> rules;

    public WalletTransferRulesPipeline(GetAccountBalanceUseCase getAccountBalanceUseCase) {
        this.rules = List.of(
                new AmountPositiveRule(),
                new IdempotencyKeyPresentRule(),
                new DifferentAccountsRule(),
                new WalletCurrencyMatchRule(),
                new SufficientBalanceRule(getAccountBalanceUseCase)
        );
    }

    public void validate(WalletTransferRuleContext context) {
        for (WalletTransferRule rule : rules) {
            var violation = rule.validate(context);
            if (violation.isPresent()) {
                throw violation.get().getException();
            }
        }
    }
}
