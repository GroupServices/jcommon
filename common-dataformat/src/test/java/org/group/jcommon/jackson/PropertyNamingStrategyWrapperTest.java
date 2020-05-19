package org.group.jcommon.jackson;

import org.group.jcommon.protobuf.jackson.PropertyNamingStrategyWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyNamingStrategyWrapperTest {

    @Test
    public void testTranslateName() {
        String[][] cases = { { "index", "index" }, { "start_at", "startAt" }, { "_start_at", "startAt" },
                { "_start_at_", "startAt" }, { "__start__at__", "startAt" },
                { "skip_all_whitespaces", "skipAllWhitespaces" }, { "original_url", "originalUrl" },
                { "original_URL", "originalURL" }, { "original_uRL", "originalURL" }, { "originalURL", "originalURL" },
                { "applyToAllSkus", "applyToAllSkus" }, { "apply_to_all_skus", "applyToAllSkus" }, };
        PropertyNamingStrategyWrapper.SnakeToCamelNamingStrategy strategy = new PropertyNamingStrategyWrapper.SnakeToCamelNamingStrategy();
        for (String[] c : cases) {
            Assertions.assertEquals(c[1], strategy.translate(c[0]));
        }
    }
}