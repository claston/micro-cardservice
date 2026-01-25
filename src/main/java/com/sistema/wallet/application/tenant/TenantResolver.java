package com.sistema.wallet.application.tenant;

import java.util.UUID;

public interface TenantResolver {
    UUID resolveTenantId(String apiKey);
}
