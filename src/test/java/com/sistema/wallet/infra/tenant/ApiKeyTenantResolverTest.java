package com.sistema.wallet.infra.tenant;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiKeyTenantResolverTest {

    @Test
    public void shouldResolveTenantId() {
        UUID tenantId = UUID.randomUUID();
        ApiKeyTenantResolver resolver = new ApiKeyTenantResolver(
                Optional.of("key-1=" + tenantId + ", key-2=" + UUID.randomUUID())
        );

        assertEquals(tenantId, resolver.resolveTenantId("key-1"));
    }

    @Test
    public void shouldRejectBlankApiKey() {
        ApiKeyTenantResolver resolver = new ApiKeyTenantResolver(Optional.of(""));

        assertThrows(IllegalArgumentException.class, () -> resolver.resolveTenantId(" "));
    }

    @Test
    public void shouldRejectUnknownApiKey() {
        ApiKeyTenantResolver resolver = new ApiKeyTenantResolver(Optional.of("key-1=" + UUID.randomUUID()));

        assertThrows(IllegalArgumentException.class, () -> resolver.resolveTenantId("missing"));
    }
}
