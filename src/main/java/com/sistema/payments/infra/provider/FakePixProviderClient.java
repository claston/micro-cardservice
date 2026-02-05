package com.sistema.payments.infra.provider;

import com.sistema.payments.application.provider.ChargeCreated;
import com.sistema.payments.application.provider.CreateChargeParams;
import com.sistema.payments.application.provider.CreatePayoutParams;
import com.sistema.payments.application.provider.PayoutCreated;
import com.sistema.payments.application.provider.PixProviderClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@ApplicationScoped
public class FakePixProviderClient implements PixProviderClient {
    @Override
    public ChargeCreated createCharge(CreateChargeParams params) {
        String externalId = "pix_charge_" + UUID.randomUUID();
        String txid = "txid_" + UUID.randomUUID();
        String copyPaste = "000201FAKEPIX" + params.getReferenceId();
        String qrCode = "data:image/png;base64,FAKEQR";
        Instant expiresAt = Instant.now().plus(30, ChronoUnit.MINUTES);
        return new ChargeCreated(externalId, txid, qrCode, copyPaste, expiresAt);
    }

    @Override
    public PayoutCreated createPayout(CreatePayoutParams params) {
        String externalId = "pix_payout_" + UUID.randomUUID();
        return new PayoutCreated(externalId);
    }
}
