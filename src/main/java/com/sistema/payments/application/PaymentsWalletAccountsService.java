package com.sistema.payments.application;

import com.sistema.wallet.application.CreateWalletAccountUseCase;
import com.sistema.wallet.application.command.CreateWalletAccountCommand;
import com.sistema.wallet.application.exception.WalletAccountAlreadyExistsException;
import com.sistema.wallet.domain.model.WalletAccount;
import com.sistema.wallet.domain.model.WalletOwnerType;
import com.sistema.wallet.domain.repository.WalletAccountRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class PaymentsWalletAccountsService {
    public static final String CASH_AT_PSP_OWNER_ID = "CASH_AT_PSP";
    public static final String OUTBOUND_CLEARING_OWNER_ID = "OUTBOUND_CLEARING";

    private final WalletAccountRepository walletAccountRepository;
    private final CreateWalletAccountUseCase createWalletAccountUseCase;

    public PaymentsWalletAccountsService(WalletAccountRepository walletAccountRepository,
                                         CreateWalletAccountUseCase createWalletAccountUseCase) {
        this.walletAccountRepository = walletAccountRepository;
        this.createWalletAccountUseCase = createWalletAccountUseCase;
    }

    public WalletAccount getOrCreateCashAtPsp(UUID tenantId, String currency) {
        return getOrCreate(tenantId, WalletOwnerType.FUNDING, CASH_AT_PSP_OWNER_ID, currency, "Cash at PSP");
    }

    public WalletAccount getOrCreateOutboundClearing(UUID tenantId, String currency) {
        return getOrCreate(tenantId, WalletOwnerType.INTERNAL, OUTBOUND_CLEARING_OWNER_ID, currency, "Outbound Clearing");
    }

    private WalletAccount getOrCreate(UUID tenantId, WalletOwnerType ownerType, String ownerId, String currency, String label) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(currency, "currency");
        return walletAccountRepository.findByOwner(tenantId, ownerType, ownerId, currency)
                .orElseGet(() -> create(tenantId, ownerType, ownerId, currency, label));
    }

    private WalletAccount create(UUID tenantId, WalletOwnerType ownerType, String ownerId, String currency, String label) {
        try {
            return createWalletAccountUseCase.execute(
                    tenantId,
                    new CreateWalletAccountCommand(ownerType, ownerId, currency, label)
            );
        } catch (WalletAccountAlreadyExistsException ex) {
            return walletAccountRepository.findByOwner(tenantId, ownerType, ownerId, currency)
                    .orElseThrow(() -> ex);
        }
    }
}
