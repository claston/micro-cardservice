package com.sistema.payments.application.provider;

public interface PixProviderClient {
    ChargeCreated createCharge(CreateChargeParams params);

    PayoutCreated createPayout(CreatePayoutParams params);
}
