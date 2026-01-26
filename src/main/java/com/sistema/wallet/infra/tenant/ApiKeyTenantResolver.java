package com.sistema.wallet.infra.tenant;

import com.sistema.wallet.application.tenant.TenantResolver;
import com.sistema.wallet.application.exception.WalletUnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ApiKeyTenantResolver implements TenantResolver {
    private final Map<String, UUID> apiKeyToTenant = new HashMap<>();

    public ApiKeyTenantResolver(@ConfigProperty(name = "wallet.api-keys") Optional<String> apiKeys) {
        String resolved = apiKeys.orElse("");
        if (resolved.isBlank()) {
            return;
        }
        String[] pairs = resolved.split(",");
        for (String pair : pairs) {
            String trimmed = pair.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String[] parts = trimmed.split("=");
            if (parts.length != 2) {
                continue;
            }
            String key = parts[0].trim();
            String value = parts[1].trim();
            if (!key.isEmpty() && !value.isEmpty()) {
                apiKeyToTenant.put(key, UUID.fromString(value));
            }
        }
    }

    @Override
    public UUID resolveTenantId(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new WalletUnauthorizedException("apiKey is required");
        }
        UUID tenantId = apiKeyToTenant.get(apiKey);
        if (tenantId == null) {
            throw new WalletUnauthorizedException("apiKey not recognized");
        }
        return tenantId;
    }
}
