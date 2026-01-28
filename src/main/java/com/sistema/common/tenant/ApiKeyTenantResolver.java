package com.sistema.common.tenant;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ApiKeyTenantResolver implements TenantResolver {
    private final Map<String, UUID> apiKeyToTenant = new HashMap<>();

    public ApiKeyTenantResolver(@ConfigProperty(name = "app.api-keys") Optional<String> appApiKeys,
                                @ConfigProperty(name = "wallet.api-keys") Optional<String> walletApiKeys) {
        String resolved = firstNonBlank(appApiKeys.orElse(null), walletApiKeys.orElse(null));
        if (resolved == null || resolved.isBlank()) {
            return;
        }
        for (String pair : resolved.split(",")) {
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
    public Optional<UUID> resolveTenantId(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(apiKeyToTenant.get(apiKey));
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}

