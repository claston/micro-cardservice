package com.sistema.wallet.application;

import com.sistema.ledger.application.GetAccountStatementUseCase;
import com.sistema.wallet.application.exception.WalletAccountNotFoundException;
import com.sistema.wallet.application.model.WalletStatementItem;
import com.sistema.wallet.application.model.WalletStatementPage;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetWalletStatementUseCase {
    private final WalletAccountRepository walletAccountRepository;
    private final GetAccountStatementUseCase getAccountStatementUseCase;

    public GetWalletStatementUseCase(WalletAccountRepository walletAccountRepository,
                                     GetAccountStatementUseCase getAccountStatementUseCase) {
        this.walletAccountRepository = walletAccountRepository;
        this.getAccountStatementUseCase = getAccountStatementUseCase;
    }

    public WalletStatementPage execute(UUID tenantId, UUID accountId, Instant from, Instant to, int page, int size) {
        WalletAccount walletAccount = walletAccountRepository.findById(tenantId, accountId)
                .orElseThrow(() -> new WalletAccountNotFoundException(accountId));

        var statement = getAccountStatementUseCase.execute(
                tenantId,
                walletAccount.getLedgerAccountId(),
                from,
                to,
                page,
                size
        );

        List<WalletStatementItem> items = statement.getItems().stream()
                .map(item -> new WalletStatementItem(
                        item.getOccurredAt(),
                        item.getTransactionId(),
                        item.getDescription(),
                        item.getDirection().name(),
                        item.getAmountMinor(),
                        item.getCurrency()
                ))
                .collect(Collectors.toList());

        return new WalletStatementPage(walletAccount.getId(), items, statement.getPage(), statement.getSize(), statement.getTotal());
    }
}
