package com.sistema.ledger.domain.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NegativeBalancePolicyTest {

    @Test
    void shouldAllowNegativeWhenFlagIsTrue() {
        assertDoesNotThrow(() -> NegativeBalancePolicy.check(true, -10));
    }

    @Test
    void shouldRejectNegativeWhenFlagIsFalse() {
        assertThrows(IllegalArgumentException.class, () -> NegativeBalancePolicy.check(false, -1));
    }

    @Test
    void shouldAllowZeroWhenFlagIsFalse() {
        assertDoesNotThrow(() -> NegativeBalancePolicy.check(false, 0));
    }
}
