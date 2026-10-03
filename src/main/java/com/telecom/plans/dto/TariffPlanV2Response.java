package com.telecom.plans.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** v2 contract: adds derived pricing fields without breaking v1 clients. */
public record TariffPlanV2Response(String code, String name, BigDecimal price, int dataGb,
                                   int validityDays, String category,
                                   BigDecimal pricePerDay, BigDecimal pricePerGb) {

    public static TariffPlanV2Response from(TariffPlanResponse p) {
        BigDecimal perDay = p.price().divide(BigDecimal.valueOf(p.validityDays()), 2, RoundingMode.HALF_UP);
        BigDecimal perGb = p.dataGb() == 0 ? BigDecimal.ZERO
                : p.price().divide(BigDecimal.valueOf(p.dataGb()), 2, RoundingMode.HALF_UP);
        return new TariffPlanV2Response(p.code(), p.name(), p.price(), p.dataGb(),
                p.validityDays(), p.category(), perDay, perGb);
    }
}
