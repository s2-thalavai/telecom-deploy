package com.telecom.plans.dto;

import com.telecom.plans.entity.TariffPlan;
import java.io.Serializable;
import java.math.BigDecimal;

/** Serializable because it is stored in Redis in staging/prod. */
public record TariffPlanResponse(Long id, String code, String name, BigDecimal price,
                                 int dataGb, int validityDays, String category, boolean active)
        implements Serializable {

    public static TariffPlanResponse from(TariffPlan p) {
        return new TariffPlanResponse(p.getId(), p.getCode(), p.getName(), p.getPrice(),
                p.getDataGb(), p.getValidityDays(), p.getCategory(), p.isActive());
    }
}
