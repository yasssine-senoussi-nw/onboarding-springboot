package com.nimbleways.springboilerplate.common.domain.ports;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class RandomGeneratorPortContractTests {

    @Test
    void returns_unique_uuids_for_a_significant_number_of_calls() {
        RandomGeneratorPort randomGenerator = getInstance();
        int count = 10_000;
        Set<UUID> uuids = new HashSet<>(count);
        for (int i=0; i< count; ++i) {
            uuids.add(randomGenerator.uuid());
        }
        assertEquals(count, uuids.size());
    }


    // --------------------------------- Protected Methods ------------------------------- //
    protected abstract RandomGeneratorPort getInstance();
}
