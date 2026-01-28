package com.sistema.customer.api;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class CustomerMultiTenantProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "app.api-keys", "key-a=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa,key-b=bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
        );
    }
}

