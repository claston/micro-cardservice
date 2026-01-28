package com.sistema.common.tenant;

import java.util.Optional;
import java.util.UUID;

public interface TenantResolver {
    Optional<UUID> resolveTenantId(String apiKey);
}

