package com.sistema.wallet.infra.tenant;

import com.sistema.common.tenant.ApiKeyTenantResolver;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiKeyTenantResolverTest {

    @Test
    public void shouldResolveTenantId() {
        UUID tenantId = UUID.randomUUID();
        ApiKeyTenantResolver resolver = new ApiKeyTenantResolver(
                Optional.of("key-1=" + tenantId + ", key-2=" + UUID.randomUUID()),
                Optional.empty()
        );

        assertEquals(Optional.of(tenantId), resolver.resolveTenantId("key-1"));
    }

    @Test
    public void shouldReturnEmptyForBlankApiKey() {
        ApiKeyTenantResolver resolver = new ApiKeyTenantResolver(Optional.of(""), Optional.empty());

        assertEquals(Optional.empty(), resolver.resolveTenantId(" "));
    }

    @Test
    public void shouldReturnEmptyForUnknownApiKey() {
        ApiKeyTenantResolver resolver = new ApiKeyTenantResolver(Optional.of("key-1=" + UUID.randomUUID()), Optional.empty());

        assertEquals(Optional.empty(), resolver.resolveTenantId("missing"));
    }
}

